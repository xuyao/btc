package cn.xy.okex.service;

import java.util.List;
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
		List<String> list = Market.list;
		for(String s : list){
			detail(s);
		}
		logger.info("okex.");
	}
	
	public void detail(String exn){
		String btcusdt = Market.BTC_USDT;
		String exnbtc = exn+"_btc";
		String exnusdt = exn+"_usdt";
		AskBid ab_exnusdt = compService.getAskBid(exnusdt);// xxx/usdt
		AskBid ab_btcusdt = compService.getAskBid(btcusdt);//btc/usdt
		AskBid ab_exnbtc = compService.getAskBid(exnbtc);// xxx/btc
		
		//第一三角
		if(ab_exnbtc.getAsk1()<(ab_exnusdt.getBid1()/ab_btcusdt.getAsk1())){
			double amount = Math.min(ab_exnbtc.getAsk1_amount(), ab_exnusdt.getBid1_amount());
			double diff = amount*ab_exnusdt.getBid1()/ab_btcusdt.getAsk1() - ab_exnbtc.getAsk1()*amount;
			if(diff>0.0000001){
				System.out.println(exnbtc+" 买入："+ab_exnbtc.getAsk1()+" "+ab_exnbtc.getAsk1_amount());
				System.out.println(exnusdt+" 卖出："+ab_exnusdt.getBid1()+" "+ab_exnusdt.getBid1_amount());
				System.out.println(btcusdt+" 买入："+ab_btcusdt.getAsk1()+" "+ab_btcusdt.getAsk1_amount());
				System.out.println("btc==============="+diff);
			}
			
		}
		
		//第二三角
		if(ab_btcusdt.getBid1()>(ab_exnusdt.getAsk1()/ab_exnbtc.getBid1())){
			double amount = Math.min(ab_exnbtc.getAsk1_amount(), ab_exnusdt.getBid1_amount());
			double diff = amount*ab_exnbtc.getBid1()-(amount*ab_exnusdt.getAsk1()/ab_btcusdt.getBid1());
			if(diff>0.0000001){
				System.out.println(btcusdt+" 卖出："+ab_btcusdt.getBid1()+" "+ab_btcusdt.getBid1_amount());
				System.out.println(exnusdt+" 买入："+ab_exnusdt.getAsk1()+" "+ab_exnusdt.getAsk1_amount());
				System.out.println(exnbtc+" 卖出："+ab_exnbtc.getBid1()+" "+ab_exnbtc.getBid1_amount());
				System.out.println("btc==============="+diff);
			}

		}
	}
}
