package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.xy.zb.service.CompService;
import cn.xy.zb.service.OrderService;

public class ZB {
	public static void main(String[] args){
		System.out.println("run zb start!!!");
//		Market.init();//市场先加载
//		zb 记得下单order，卖出时候只跑一次，其他都是三次
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContent.xml");
		CompService compService = (CompService)context.getBean("compService");
		OrderService orderService = (OrderService)context.getBean("orderService");
	}
}
