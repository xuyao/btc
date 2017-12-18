package cn.xy.zb.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.zb.Market;
import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.util.DateUtil;
import cn.xy.zb.util.NumberUtil;
import cn.xy.zb.vo.AccountInfo;
import cn.xy.zb.vo.AskBid;
import cn.xy.zb.vo.Deal;

@Service
public class JobService {

	@Autowired
	CompService compService;
	@Autowired
	OrderService orderService;
	AccountInfo ai = null;
	
	public void work(){
//		long a = System.currentTimeMillis();
		ai = compService.getAccountInfo();
		String[][] arry = Market.arry;
		for(String[] sa : arry){
			detail(sa[0], sa[1]);
		}
//		System.out.println((System.currentTimeMillis()-a)/1000);
		
	}
	
	public void detail(String abqc, String abusdt){
		AskBid ab_qc = compService.getAskBid(abqc);//qc叫价
		AskBid ab_usdt = compService.getAskBid(abusdt);//usdt叫价
		if(ab_qc==null || ab_usdt==null)
			return ;//如果为空，就返回
		
		Deal deal_ac_usdt = compService.compCnyUsd(ab_qc, ab_usdt);//cny转usd
		Deal deal_usdt_qc = compService.compUsdCny(ab_usdt, ab_qc);
		
//		orderService.dealQc2Usdt(deal_ac_usdt, ai);
		orderService.dealUsdt2Qc(deal_usdt_qc, ai);

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
