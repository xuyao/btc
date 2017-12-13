package cn.xy.btc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.xy.btc.service.CompService;
import cn.xy.btc.util.ConstsUtil;
import cn.xy.btc.util.HttpUtil;
import cn.xy.btc.vo.AskBid;
import cn.xy.btc.vo.Deal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class Run {

	
	
	public static void main(String[] args){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContent.xml");
		CompService compService = (CompService)context.getBean("compService");
		Double usd_cny = ConstsUtil.getCnyUsd("usd_cny");//汇率
		Market.init();
//		for(String[] arr : Market.arry){
//			System.out.println(arr[0]+" "+arr[1]);
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		}
		
		AskBid ab_btsqc = compService.getAskBid("bts_qc");
		AskBid ab_btsusdt = compService.getAskBid("bts_usdt",usd_cny);
		
		
		Deal deal = compService.comp(ab_btsqc, ab_btsusdt);
		if(deal==null)
			deal = compService.comp(ab_btsusdt, ab_btsqc);
		if(deal!=null){
			StringBuilder sb = new StringBuilder();
			sb.append(" 买入市场：").append(deal.getBuyMarket());
			sb.append(" ").append("买入价格：").append(deal.getBuyPrice());
			sb.append(" ").append("买入量：").append(deal.getBuyAmount());
			sb.append("卖出市场：").append(deal.getSellMarket());
			sb.append(" ").append("卖出价格：").append(deal.getSellPrice());
			sb.append(" ").append("卖出量：").append(deal.getSellAmount());
			System.out.println(sb.toString());
		}
		
	}
}
