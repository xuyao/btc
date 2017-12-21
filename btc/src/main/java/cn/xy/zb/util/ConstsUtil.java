package cn.xy.zb.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConstsUtil {
	
	public static final Properties prop = new Properties();
	
	static{
		InputStream is = ConstsUtil.class.getClassLoader().getResourceAsStream("conf.properties");
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getValue(String key){
		return prop.getProperty(key);
	}
	
	public static Double getCnyUsd(){
		return Double.parseDouble(prop.getProperty("usd_cny"));
	}
	
	public static Double getCompCnyUsd(){
		return Double.parseDouble(prop.getProperty("comp_cny_usd"));
	}
	
	public static Double getCompUsdCny(){
		return Double.parseDouble(prop.getProperty("comp_usd_cny"));
	}
	
	public static Double getQcLimit(){
		return Double.parseDouble(prop.getProperty("qc_limit"));
	}
	
	public static Double getUsdtLimit(){
		return Double.parseDouble(prop.getProperty("usdt_limit"));
	}
	
	public static Double getProfit(){
		return Double.parseDouble(prop.getProperty("profit"));
	}
	
	public static String getSniff(){
		return prop.getProperty("sniff");
	}

}
