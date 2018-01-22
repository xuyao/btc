package cn.xy.zb.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.xy.zb.AutoSell;
import cn.xy.zb.Market;
import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.util.NumberUtil;
import cn.xy.zb.vo.MarketAB;
import cn.xy.zb.vo.Order;
import cn.xy.zb.vo.Ticker;

@Service
public class CancelService extends LogService{

	@Autowired
	CompService compService;
	@Autowired
	OrderService orderService;
	@Autowired
	HttpService httpService;
	
	Double usd_cny = ConstsUtil.getCnyUsd();//汇率
	
	public void work(){
		//查询账户, 先处理下余额
		doRemain();
		
		//循环市场,处理冻结
		List<Order> orderList = null;
		String[][] arry = AutoSell.arry;
		for(String[] sa : arry){//循环市场
			orderList = orderService.getUnfinishedOrdersIgnoreTradeType(sa[0]);
			doCancelOrder(orderList, sa);
			
			orderList = orderService.getUnfinishedOrdersIgnoreTradeType(sa[1]);
			doCancelOrder(orderList, sa);
			try {
				Thread.sleep(350);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("*");
	}

	
	private void doRemain(){
		try {
			// 需加密的请求参数
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "getAccountInfo");
			String json = httpService.getJsonPost(params);
			JSONObject result = JSON.parseObject(json);
			if(result == null)
				return;
			result = result.getJSONObject("result");
			JSONArray jsonArry = result.getJSONArray("coins");
			
			Iterator it = jsonArry.iterator();
			String market = "";
			Double available = 0.0;
			while(it.hasNext()) {
				JSONObject jsonObj = (JSONObject)it.next();
				market = jsonObj.getString("key");
				available = jsonObj.getDouble("available");
				Double amount = getAmount(market+"_qc", available);
				if(amount == null)
					continue;
				
				if(amount>0) {//如果有剩余数量，就要询价卖出
					doOrder(market, amount);
					Thread.sleep(200);
				}
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//得到相应市场的买入数量
	public Double getAmount(String market, Double amount){
		MarketAB mab = AutoSell.map.get(market);
		if(mab==null)
			return null;
		return NumberUtil.formatDouble(amount, mab.getAmountScale());
	}
	
	private void doOrder(String market, Double amount) {
		Ticker tqc = compService.getTicker(market+"_qc");
		Ticker tusdt = compService.getTicker(market+"_usdt");
		if(tqc.getLast()>tusdt.getLast()*usd_cny) {//qc贵
			orderService.order(market+"_qc", "0", String.valueOf(tqc.getLast()), String.valueOf(amount));
		}else {
			orderService.order(market+"_usdt", "0", String.valueOf(tusdt.getLast()), String.valueOf(amount));
		}
	}
	
	private void doOrder(Order o, String market, Double amount) {
		Ticker tqc = compService.getTicker(market+"_qc");
		Ticker tusdt = compService.getTicker(market+"_usdt");
		if(tqc.getLast()==o.getPrice() || tusdt.getLast()==o.getPrice()) {//如果挂单价格和当前价格一样，什么也不做
			//noting to do
		}else {//否则应该先撤单再比较，然后下单
			orderService.cancelOrder(o);
			if(tqc.getLast()>tusdt.getLast()*usd_cny) {//qc贵
				orderService.order(market+"_qc", "0", String.valueOf(tqc.getLast()), String.valueOf(amount));
			}else {
				orderService.order(market+"_usdt", "0", String.valueOf(tusdt.getLast()), String.valueOf(amount));
			}
		}
	}
	
	
	private void doCancelOrder(List<Order> orderList, String[] sa){
		if(orderList==null)//如果没有未成交单据，直接返回
			return;
		for(Order o : orderList){
			if(o.getType()==1){//如果是买单未成交，无论什么情况立刻撤销
				orderService.cancelOrder(o);
			}else if(o.getType()==0){//如果是卖单未成交，处理起来比较麻烦
				//如果时间够长，才能撤单
				long sys = System.currentTimeMillis();
				if(sys - o.getTrade_date() > 240*1000) {//如果订单间隔2分钟，就撤单
					doOrder(o, o.getCurrency().substring(0, o.getCurrency().indexOf("_")), (o.getTotal_amount()-o.getTrade_amount()));
				}
			}else{
				System.out.println("Ask me please,why this order type is undefinded?");
			}
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public CompService getCompService() {
		return compService;
	}

	public void setCompService(CompService compService) {
		this.compService = compService;
	}
	
}
