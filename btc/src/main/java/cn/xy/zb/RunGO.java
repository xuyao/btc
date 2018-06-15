package cn.xy.zb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunGO {

	public static void main(String[] args){
		System.out.println("run zb start!!!");
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContent.xml");
	}
}
