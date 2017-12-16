package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Run {

	public static void main(String[] args){
		System.out.println("运行开始！");
		Market.init();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContent.xml");

	}
}
