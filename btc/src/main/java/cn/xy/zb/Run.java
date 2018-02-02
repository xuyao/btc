package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.xy.zb.service.CompService;
import cn.xy.zb.service.OrderService;

public class Run {

	public static void main(String[] args){
		System.out.println("run zb start!!!");
		Market.init();//市场先加载
//		Tax.init();//税后加载
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContent.xml");
		CompService compService = (CompService)context.getBean("compService");
		OrderService orderService = (OrderService)context.getBean("orderService");
		compService.usd_cny = compService.getTicker("usdt_qc").getLast();
		orderService.usd_cny = compService.getTicker("usdt_qc").getLast();
		
	}
}
