package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.xy.zb.service.CompService;
import cn.xy.zb.service.OrderService;

public class Run {

	public static void main(String[] args){
		System.out.println("run zb start!!!");
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContent.xml");
	}
}
