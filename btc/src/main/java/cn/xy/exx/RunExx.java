package cn.xy.exx;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunExx {

	public static void main(String[] args){
		System.out.println("run exx start!!!");
		Market.init();//市场先加载
//		Tax.init();//税后加载
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContentExx.xml");
	}
}
