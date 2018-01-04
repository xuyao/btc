package cn.xy.exx.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.exx.Market;
import cn.xy.exx.util.ConstsUtil;
import cn.xy.exx.vo.AccountInfo;
import cn.xy.exx.service.LogService;
import cn.xy.exx.vo.Order;

@Service
public class CancelService extends LogService{

	@Autowired
	CompService compService;
	@Autowired
	OrderService orderService;
	
	Double usd_cny = ConstsUtil.getCnyUsd();//汇率
	
	AccountInfo ai = null;
	
	public void work(){
		//查询账户
		ai = compService.getAccountInfo();
		
//		//循环市场
		List<Order> orderList = null;
		String[][] arry = Market.arry;
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
	}

	
	private void doCancelOrder(List<Order> orderList, String[] sa){
		if(orderList==null)//如果没有未成交单据，直接返回
			return;
		for(Order o : orderList){
			if("buy".equals(o.getType())){//如果是买单未成交，无论什么情况立刻撤销
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				orderService.cancelOrder(o);
			}else if("sell".equals(o.getType())){//如果是卖单未成交，处理起来比较麻烦
//				//如果时间够长，才能撤单
//				long sys = System.currentTimeMillis();
//				if(sys - o.getTrade_date() < 240000)//如果订单间隔2分钟，就撤单
//					return;
//				orderService.cancelOrder(o);
//				AskBid ab_qc = compService.getAskBid(sa[0]);//qc叫价
//				AskBid ab_usdt = compService.getAskBid(sa[1]);//usdt叫价
//				if(ab_qc.getBid1()>ab_usdt.getBid1()*usd_cny){//人民币市场价格大于美元
//					Deal deal_ac_usdt = compService.compCnyUsd(ab_qc, ab_usdt);
//					orderService.dealQc2Usdt(deal_ac_usdt, ai);
//				}else{//美元价格大于人民币的话，卖向美元市场
//					Deal deal_usdt_qc = compService.compUsdCny(ab_usdt, ab_qc);
//					orderService.dealUsdt2Qc(deal_usdt_qc, ai);
//				}
			}else{
				System.out.println("Ask me please,why this order type is undefinded?");
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
