package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunCancel {

	public static void main(String[] args){
		System.out.println("run zb order cancel start!!!");
		AutoSell.init();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContentCancel.xml");
	}
}
