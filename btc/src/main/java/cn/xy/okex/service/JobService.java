package cn.xy.okex.service;

import java.util.HashMap;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.okex.Market;
import cn.xy.okex.vo.AskBid;

@Service
public class JobService extends LogService{

	@Autowired
	CompService compService;
	@Autowired
	OrderService orderService;
	
//	AccountInfo ai = null;
	
	public void work(){
		//查询账户
//		ai = compService.getAccountInfo();
		//循环市场
		HashMap<String,String> map = Market.map;
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String exn = (String)it.next();
			String exn2 = map.get(exn);
			detail(exn, exn2);
		}
		logger.info("okex.");
	}
	
	public void detail(String exn, String exnbtc){
		String btce = exn.split("_")[0]+"_btc";
		AskBid ab_exn = compService.getAskBid(exn);//交易币对usdt的报价
		AskBid ab_exn2 = compService.getAskBid(exnbtc);//btc对usdt的报价
		
		AskBid ab_btce = compService.getAskBid(btce);//交易币对btc的报价
		ab_exn2.getAsk1();//卖一价格
		ab_btce.getAsk1();//卖一价格,btc价格
		ab_exn.getBid1();//买一
		//于是开启买买卖
		if((ab_exn.getBid1()/ab_exn2.getAsk1())>ab_btce.getAsk1()){
//			amount = Math.min(amount, ab_exn.getBid1_amount());
			System.out.println(exnbtc+" 买入："+ab_exn2.getAsk1());
			System.out.println(btce+" 买入："+ab_btce.getAsk1());
			System.out.println(exn+" 卖出："+ab_exn.getBid1());
			System.out.println("======================");
		}
	}
	
}
