package cn.xy.okex;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Run {

	public static void main(String[] args){
		System.out.println("run okex start!!!");
		Market.init();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContentokex.xml");
	}
}
