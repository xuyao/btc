package cn.xy.btc.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.btc.util.ConstsUtil;
import cn.xy.btc.util.DateUtil;
import cn.xy.btc.util.NumberUtil;
import cn.xy.btc.vo.AccountInfo;
import cn.xy.btc.vo.AskBid;
import cn.xy.btc.vo.Deal;

@Service
public class JobService {

	@Autowired
	CompService compService;
	Double usd_cny = ConstsUtil.getCnyUsd();//汇率
	Double qc_limit = ConstsUtil.getQcLimit();//汇率
	
	public void work(){
		detail("bts_qc","bts_usdt");
//		detail("btc_qc","btc_usdt");
		detail("xrp_qc","xrp_usdt");
		detail("hsr_qc","hsr_usdt");
		detail("eos_qc","eos_usdt");
		detail("bcc_qc","bcc_usdt");
		detail("ltc_qc","ltc_usdt");
		detail("eth_qc","eth_usdt");
		detail("etc_qc","etc_usdt");
		detail("qtum_qc","qtum_usdt");
		detail("bcd_qc","bcd_usdt");
		detail("dash_qc","dash_usdt");
		
		
//		AccountInfo ai = compService.getAccountInfo();
//		System.out.println(ai.getQcAvailable()+" "+ai.getUsdtAvailable());
		//compService.order("bts_qc","1","1","10");
	}
	
	public void detail(String abqc, String abusdt){
		AskBid ab_qc = compService.getAskBid(abqc);
		AskBid ab_usdt = compService.getAskBid(abusdt);
		if(ab_qc==null || ab_usdt==null)
			return ;//如果为空，就返回
		Deal deal = compService.compCnyUsd(ab_qc, ab_usdt ,usd_cny);//cny转usd
		if(deal==null)
			deal = compService.compUsdCny(ab_usdt, ab_qc ,usd_cny);
		if(deal!=null){
			Double buyPrice = deal.getBuyPrice();
			Double sellPrice = deal.getSellPrice();
			if(deal.getBuyMarket().contains("qc"))//如果买入方是qc的话，卖出价格应该汇率
				sellPrice = sellPrice*usd_cny;
			if(deal.getBuyMarket().contains("usdt"))//如果买入方是usdt，买入价格应该汇率
				buyPrice = buyPrice*usd_cny;
			
			StringBuilder sb = new StringBuilder(DateUtil.formatLongPattern(new Date()));
			sb.append(" 买入市场：").append(deal.getBuyMarket());
			sb.append(" ").append(deal.getBuyPrice()).append(" ").append(buyPrice);
			sb.append(" ").append(deal.getBuyAmount());
			Double amount = qc_limit/buyPrice;
			if(amount>deal.getBuyAmount())//如果限制仓位下的amount小于挂单买入的amount，以小的为准
				amount=deal.getBuyAmount();
			sb.append(" ").append(amount);
			sb.append(" 卖出市场：").append(deal.getSellMarket());
			sb.append(" ").append(deal.getSellPrice()).append(" ").append(sellPrice);;
			sb.append(" ").append(deal.getSellAmount());
			sb.append(" ").append(amount);
			sb.append(" 利：").append((sellPrice-buyPrice)*deal.getSellAmount()*0.998);//手续费
			System.out.println(sb.toString());
			
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
		}
		
	}

	
	
	public CompService getCompService() {
		return compService;
	}

	public void setCompService(CompService compService) {
		this.compService = compService;
	}
	
}
