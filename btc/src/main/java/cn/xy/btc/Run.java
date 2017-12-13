package cn.xy.btc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Run {

	public static void main(String[] args){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContent.xml");
		Market.init();
//		for(String[] arr : Market.arry){
//		System.out.println(arr[0]+" "+arr[1]);
//	try {
//		Thread.sleep(500);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
//	}
		
	}
}
