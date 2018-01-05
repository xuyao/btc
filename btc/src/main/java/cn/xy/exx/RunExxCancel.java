package cn.xy.exx;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunExxCancel {

	public static void main(String[] args){
		System.out.println("run exx order cancel start!!!");
		Market.init();//市场先加载
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContentCancelExx.xml");
	}
}
