package cn.xy.okex;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Market {

	public static String BTC_USDT = "btc_usdt";
	public static List<String> list;
	
	public static void init(){
		URL url = Market.class.getClassLoader().getResource("");
	    File file = new File(url.getFile()+File.separator+"cn/xy/okex/c.txt");
	    
		try {
			list = FileUtils.readLines(file,"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
//	    for(String s : list){
//	    	System.out.println(s);
//	    }
	}
}
