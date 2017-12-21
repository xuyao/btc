package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Run {

	public static void main(String[] args){
		System.out.println("run start!!!");
		Market.init();//市场先加载
		Tax.init();//税后加载
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContent.xml");
	}
}
