package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class QCUSDT {
	
	public static void main(String[] args){
		System.out.println("run qcusdt start!!!");
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContentQCUSD.xml");
	}
	


}
