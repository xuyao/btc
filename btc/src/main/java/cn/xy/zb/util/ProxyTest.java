package cn.xy.zb.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class ProxyTest {

	public static void main(String[] args){
		//http://www.ip138.com/ips138.asp
		//http://api.zb.com/data/v1/ticker?market=btc_usdt
//		get("http://api.zb.com/data/v1/ticker?market=btc_usdt", "f", 
//				args[0], args[1]);
//		long a = System.currentTimeMillis();
//		System.out.println(get("http://api.zb.com/data/v1/ticker?market=btc_usdt", "f",
//				args[0], args[1]));
//		System.out.println(System.currentTimeMillis()-a);
//		long b = System.currentTimeMillis();
//		System.out.println(get("http://api.zb.com/data/v1/ticker?market=btc_usdt", "t",
//				args[0], args[1]));
//		System.out.println(System.currentTimeMillis()-b);
		
		String host = "183.57.36.87";
		String port = "8888";
		get("http://api.zb.com/data/v1/ticker?market=btc_usdt", "f", host, port);
		long a = System.currentTimeMillis();
		System.out.println(get("http://api.zb.com/data/v1/ticker?market=btc_usdt", "f", host, port));
		System.out.println(System.currentTimeMillis()-a);
		long b = System.currentTimeMillis();
		System.out.println(get("http://api.zb.com/data/v1/ticker?market=btc_usdt", "t", host, port));
		System.out.println(System.currentTimeMillis()-b);
	}
	
	
	public static String get(String urlAll, String isproxy, String host, String port) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";// 模拟浏览器
		try {
			URL url = new URL(urlAll);
			HttpURLConnection connection = null;
			if("t".equals(isproxy)) {
		        System.setProperty("http.maxRedirects", "50");
		        System.getProperties().setProperty("proxySet", "true");
		        System.getProperties().setProperty("http.proxyHost", host);
		        System.getProperties().setProperty("http.proxyPort", port);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, Integer.parseInt(port)));  
				connection = (HttpURLConnection)url.openConnection(proxy);
			}else {
				connection = (HttpURLConnection) url.openConnection();
			}
			connection.setRequestMethod("GET");
			connection.setReadTimeout(30000);
			connection.setConnectTimeout(30000);
			connection.setRequestProperty("User-agent", userAgent);
			connection.connect();
			InputStream is = null;
			try{
				is = connection.getInputStream();
			}catch(Exception e){
				
			}
			if(is==null)
				return null;
			reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
