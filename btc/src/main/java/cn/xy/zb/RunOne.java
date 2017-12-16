package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.xy.zb.service.CompService;
import cn.xy.zb.service.OrderService;
import cn.xy.zb.vo.AskBid;
import cn.xy.zb.vo.Deal;

public class RunOne {

	//从一个市场买，从另一个市场卖
	public static void main(String[] args){
		if(args.length!=2){
			System.out.println("参数不正确");
			return ;
		}
		
		Market.init();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContentOne.xml");
		CompService compService = (CompService)context.getBean("compService");
		OrderService orderService = (OrderService)context.getBean("orderService");
		
		if(args[0].contains("qc") && args[1].contains("usdt")){//从qc买入到usdt卖出
			AskBid ab1 = compService.getAskBid(args[0]);
			AskBid ab2 = compService.getAskBid(args[1]);
			if(ab1==null || ab2==null)
				return ;//如果为空，就返回
			Deal deal_ac_usdt = compService.compCnyUsd(ab1, ab2);//cny转usd
			orderService.dealQc2Usdt(deal_ac_usdt);
		}
		
		if(args[0].contains("usdt") && args[1].contains("qc")){//从usdt买入到qc卖出
			AskBid ab1 = compService.getAskBid(args[0]);
			AskBid ab2 = compService.getAskBid(args[1]);
			if(ab1==null || ab2==null)
				return ;//如果为空，就返回
			Deal deal_usdt_qc = compService.compUsdCny(ab1, ab2);
			orderService.dealUsdt2Qc(deal_usdt_qc);
		}
		
	}
}
