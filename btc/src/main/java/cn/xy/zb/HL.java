package cn.xy.zb;

import java.util.Iterator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.xy.zb.service.HttpService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class HL {
	public static void main(String[] args){
		System.out.println("run hl start!!!");
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContentHL.xml");
	}
}
