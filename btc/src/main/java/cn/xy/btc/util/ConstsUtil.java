package cn.xy.btc.util;

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
	
	public static Double getCnyUsd(String key){
		return Double.parseDouble(prop.getProperty(key));
	}
}
