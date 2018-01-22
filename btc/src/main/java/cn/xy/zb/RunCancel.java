package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunCancel {

	public static void main(String[] args){
		System.out.println("run zb order cancel start!!!");
		Market.init();//市场先加载
		AutoSell.init();
		Tax.init();//税后加载
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContentCancel.xml");
	}
}
