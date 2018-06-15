package cn.xy.exx.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.exx.AutoSell;
import cn.xy.exx.Market;
import cn.xy.exx.util.ConstsUtil;
import cn.xy.exx.util.NumberUtil;
import cn.xy.exx.vo.AskBid;
import cn.xy.exx.vo.MarketAB;
import cn.xy.exx.vo.Order;
import cn.xy.exx.vo.Ticker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Service
public class CancelService extends LogService{

	@Autowired
	CompService compService;
	@Autowired
	OrderService orderService;
	@Autowired
	HttpService httpService;
	
	Double usd_cny = ConstsUtil.getCnyUsd();//汇率
	Integer second = ConstsUtil.getSecond();//汇率
	String autosellon = ConstsUtil.getValue("autosellon");//汇率
	String markets = ConstsUtil.getValue("market");
	
	public void work(){
		
//		//循环市场
		List<Order> orderList = null;
		String[][] arry = Market.arry;
		for(String[] sa : arry){//循环市场
			orderList = orderService.getUnfinishedOrdersIgnoreTradeType(sa[0]);
			doCancelOrder(orderList);
			
			orderList = orderService.getUnfinishedOrdersIgnoreTradeType(sa[1]);
			doCancelOrder(orderList);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//查询账户, 先处理下余额
		doRemain();
		logger.info("*");
	}

	
	private void doRemain(){
		String ha = "https://trade.exxvip.com/api/getBalance";
			// 需加密的请求参数
			Map<String, String> params = new TreeMap<String, String>();
			String json = httpService.get(ha, params);
			JSONObject result = JSON.parseObject(json);
			if(result == null)
				return;
			result = result.getJSONObject("funds");
			
			
			String[] marketArr = markets.split(",");
			Double available = 0.0;
			for (String market : marketArr) {  
//				    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
				available = result.getJSONObject(market.toUpperCase()).getDouble("balance");
				Double amount = getAmount(market+"_cnyt", available);
				if(amount == null)
					continue;
				if(amount>0) {//如果有剩余数量，就要询价卖出
					doOrder(market, amount);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} 
	}
	
	
	//得到相应市场的买入数量
	public Double getAmount(String market, Double amount){
		MarketAB mab = AutoSell.map.get(market);
		if(mab==null)
			return null;
		return NumberUtil.formatDouble(amount, mab.getAmountScale());
	}
	
	//看看qc高还是usdt高
	private void doOrder(String market, Double amount) {
		AskBid abc = compService.getAskBid(market+"_cnyt");
		AskBid abu = compService.getAskBid(market+"_usdt");
		
		if(abc.getAsk1()>abu.getAsk1()*usd_cny) {//cnyt贵
			if(abc.getAsk1()-getMinPrice(market+"_cnyt")>abc.getBid1()) {
				orderService.order(market+"_cnyt", "sell", 
						String.valueOf(abc.getAsk1()-getMinPrice(market+"_cnyt")), String.valueOf(amount));
			}else {
				orderService.order(market+"_cnyt", "sell", 
						String.valueOf(abc.getAsk1()), String.valueOf(amount));
			}

		}else {
			if(abu.getAsk1()-getMinPrice(market+"_usdt")>abu.getBid1()) {
				orderService.order(market+"_usdt", "sell", 
						String.valueOf(abu.getAsk1()-getMinPrice(market+"_usdt")), String.valueOf(amount));
			}else {
				orderService.order(market+"_usdt", "sell", 
						String.valueOf(abu.getAsk1()), String.valueOf(amount));
			}

		}
	}
	
	private void doOrder(Order o, String market, Double amount) {
		AskBid abc = compService.getAskBid(market+"_cnyt");
		AskBid abu = compService.getAskBid(market+"_usdt");
		
		if(abc.getAsk1()==o.getPrice() || abu.getAsk1()==o.getPrice()) {//如果挂单价格和卖一价格一样，什么也不做
			//noting to do
		}else {//否则应该先撤单再比较，然后下单
			orderService.cancelOrder(o);
			if(abc.getAsk1()>abu.getAsk1()*usd_cny) {//qc贵
				orderService.order(market+"_cnyt", "sell", 
						String.valueOf(abc.getAsk1()-getMinPrice(market+"_cnyt")), String.valueOf(amount));
			}else {
				orderService.order(market+"_usdt", "sell", 
						String.valueOf(abu.getAsk1()-getMinPrice(market+"_usdt")), String.valueOf(amount));
			}
		}
	}
	
	public Double getMinPrice(String market){
		MarketAB mab = AutoSell.map.get(market);
		if(mab==null)
			return null;
		return Math.pow(10, -mab.getPriceScale());
	}
	
	private void doCancelOrder(List<Order> orderList){
		if(orderList==null)//如果没有未成交单据，直接返回
			return;
		for(Order o : orderList){
			if("buy".equals(o.getType())){//如果是买单未成交，无论什么情况立刻撤销
				orderService.cancelOrder(o);
			}else if("sell".equals(o.getType())){//如果是卖单未成交，处理起来比较麻烦
				if("t".equals(autosellon)){
					//如果时间够长，才能撤单
					long sys = System.currentTimeMillis();
					if(sys - o.getTrade_date() > second*1000) {//如果订单间隔2分钟，就撤单
						doOrder(o, o.getCurrency().substring(0, o.getCurrency().indexOf("_")), (o.getTotal_amount()-o.getTrade_amount()));
					}
				}
			}else{
				System.out.println("Ask me please,why this order type is undefinded?");
			}
			
			try {
				Thread.sleep(200);
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
