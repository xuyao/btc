package cn.xy.btc.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.btc.util.ConstsUtil;
import cn.xy.btc.util.DateUtil;
import cn.xy.btc.vo.AccountInfo;
import cn.xy.btc.vo.AskBid;
import cn.xy.btc.vo.Deal;

@Service
public class JobService {

	@Autowired
	CompService compService;
	
	public void work(){
		Double usd_cny = ConstsUtil.getCnyUsd("usd_cny");//汇率
//		detail("bts_qc","bts_usdt",usd_cny);
//		detail("btc_qc","btc_usdt",usd_cny);
//		detail("xrp_qc","xrp_usdt",usd_cny);
//		detail("hsr_qc","hsr_usdt",usd_cny);
//		detail("eos_qc","eos_usdt",usd_cny);
		
		detail("bcc_qc","bcc_usdt",usd_cny);
		detail("ltc_qc","ltc_usdt",usd_cny);
		detail("eth_qc","eth_usdt",usd_cny);
		detail("etc_qc","etc_usdt",usd_cny);
		detail("qtum_qc","qtum_usdt",usd_cny);
		detail("bcd_qc","bcd_usdt",usd_cny);
		detail("dash_qc","dash_usdt",usd_cny);
		
		
//		AccountInfo ai = compService.getAccountInfo();
//		System.out.println(ai.getQcAvailable()+" "+ai.getUsdtAvailable());
	}
	
	public void detail(String abqc, String abusdt, Double usd_cny){
		AskBid ab_qc = compService.getAskBid(abqc);
		AskBid ab_usdt = compService.getAskBid(abusdt);
		if(ab_qc==null || ab_usdt==null)
			return ;//如果为空，就返回
		Deal deal = compService.compCnyUsd(ab_qc, ab_usdt ,usd_cny);
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
			sb.append(" 卖出市场：").append(deal.getSellMarket());
			sb.append(" ").append(deal.getSellPrice()).append(" ").append(sellPrice);;
			sb.append(" ").append(deal.getSellAmount());
			sb.append(" 利润CNY：").append((sellPrice-buyPrice)*deal.getSellAmount()*0.998);//手续费
			System.out.println(sb.toString());
		}
		
	}

	
	
	public CompService getCompService() {
		return compService;
	}

	public void setCompService(CompService compService) {
		this.compService = compService;
	}
	
}
