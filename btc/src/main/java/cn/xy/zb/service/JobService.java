package cn.xy.zb.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	public void work(){
//		long a = System.currentTimeMillis();
		detail("btc_qc","btc_usdt");
		detail("bcc_qc","bcc_usdt");
		detail("ubtc_qc","ubtc_usdt");
		detail("bts_qc","bts_usdt");
		detail("xrp_qc","xrp_usdt");
		detail("hsr_qc","hsr_usdt");
		detail("eos_qc","eos_usdt");
		detail("ltc_qc","ltc_usdt");
		detail("eth_qc","eth_usdt");
		detail("etc_qc","etc_usdt");
		detail("qtum_qc","qtum_usdt");
		detail("bcd_qc","bcd_usdt");
		detail("dash_qc","dash_usdt");
		detail("sbtc_qc","sbtc_usdt");
		detail("ink_qc","ink_usdt");
		detail("tv_qc","tv_usdt");
		detail("bcx_qc","bcx_usdt");
		detail("bth_qc","bth_usdt");
		detail("lbtc_qc","lbtc_usdt");
		
//		System.out.println((System.currentTimeMillis()-a)/1000);
		
//		AccountInfo ai = compService.getAccountInfo();
//		System.out.println(ai.getQcAvailable()+" "+ai.getUsdtAvailable());
		//compService.order("bts_qc","1","1","10");
	}
	
	public void detail(String abqc, String abusdt){
		AskBid ab_qc = compService.getAskBid(abqc);//qc叫价
		AskBid ab_usdt = compService.getAskBid(abusdt);//usdt叫价
		if(ab_qc==null || ab_usdt==null)
			return ;//如果为空，就返回
		
		Deal deal_ac_usdt = compService.compCnyUsd(ab_qc, ab_usdt);//cny转usd
		Deal deal_usdt_qc = compService.compUsdCny(ab_usdt, ab_qc);
		
		orderService.dealQc2Usdt(deal_ac_usdt);
		orderService.dealUsdt2Qc(deal_usdt_qc);

//			if(amount>0.0001){
//				amount = NumberUtil.formatDouble4(amount);
//				System.out.println("***********"+amount);
//				compService.order(deal.getBuyMarket(), "1", String.valueOf(deal.getBuyPrice()), String.valueOf(amount));
//				for(int i=0;i<5;i++)
//				try {
//					Thread.sleep(300);
//					compService.order(deal.getSellMarket(), "0", String.valueOf(deal.getSellPrice()), String.valueOf(amount));
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				Thread.sleep(700);
//				compService.order(deal.getSellMarket(), "0", String.valueOf(deal.getSellPrice()), String.valueOf(amount));
//			}
			
			//这里有个问题就是调整买入的数量
//		}
		
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
