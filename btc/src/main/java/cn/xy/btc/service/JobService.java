package cn.xy.btc.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.btc.util.ConstsUtil;
import cn.xy.btc.util.DateUtil;
import cn.xy.btc.vo.AskBid;
import cn.xy.btc.vo.Deal;

@Service
public class JobService {

	@Autowired
	CompService compService;
	
	public void work(){
		Double usd_cny = ConstsUtil.getCnyUsd("usd_cny");//汇率
		
		AskBid ab_btsqc = compService.getAskBid("bts_qc");
		AskBid ab_btsusdt = compService.getAskBid("bts_usdt",usd_cny);
		
		Deal deal = compService.comp(ab_btsqc, ab_btsusdt);
		if(deal==null)
			deal = compService.comp(ab_btsusdt, ab_btsqc);
		if(deal!=null){
			StringBuilder sb = new StringBuilder(DateUtil.formatLongPattern(new Date()));
			sb.append(" 买入市场：").append(deal.getBuyMarket());
			sb.append(" ").append("买入价格：").append(deal.getBuyPrice());
			sb.append(" ").append("买入量：").append(deal.getBuyAmount());
			sb.append("卖出市场：").append(deal.getSellMarket());
			sb.append(" ").append("卖出价格：").append(deal.getSellPrice());
			sb.append(" ").append("卖出量：").append(deal.getSellAmount());
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
