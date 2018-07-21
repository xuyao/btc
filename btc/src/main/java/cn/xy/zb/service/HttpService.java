package cn.xy.zb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.http.HttpException;
import org.springframework.stereotype.Service;

import cn.xy.zb.util.ConstsUtil;

import com.zb.kits.EncryDigestUtil;
import com.zb.kits.HttpUtilManager;
import com.zb.kits.MapSort;

@Service
public class HttpService extends LogService{
	
	public final String ACCESS_KEY = "";
	public final String SECRET_KEY = "";
	public final String URL_PREFIX = "https://trade.bitkk.com/api/";// 测试环境,测试环境是ttapi测试不通
	
	String isproxy = ConstsUtil.getValue("isproxy");
	static String host =  ConstsUtil.getValue("host");
	static String port =  ConstsUtil.getValue("port");
	/**
	 * 获取json内容(统一加密)
	 * 
	 * @param params
	 * @return
	 */
	public String getJsonPost(Map<String, String> params) {
		params.put("accesskey", ACCESS_KEY);// 这个需要加入签名,放前面
		String digest = EncryDigestUtil.digest(SECRET_KEY);

		String sign = EncryDigestUtil.hmacSign(MapSort.toStringMap(params), digest); // 参数执行加密
		String method = params.get("method");

		// 加入验证
		params.put("sign", sign);
		params.put("reqTime", System.currentTimeMillis() + "");
		String json = "";
		try {
			json = HttpUtilManager.getInstance().requestHttpPost(URL_PREFIX, method, params);
		} catch (HttpException | IOException e) {
			logger.error("获取交易json异常", e);
		}
		return json;
	}
	
	public String get(String urlAll) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "User-Agent:Mozilla/5.0(Macintosh;IntelMacOSX10_7_0)AppleWebKit/535.11(KHTML,likeGecko)Chrome/17.0.963.56Safari/535.11";// 模拟浏览器
		try {
			URL url = new URL(urlAll);
			HttpURLConnection connection = null;
			if("t".equals(isproxy)) {
				if(!checkProxy(host, Integer.parseInt(port))){
					logger.info("proxy is not work!");
					return null;
				}
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
				e.printStackTrace();
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
	
	//check proxy is working now
	public boolean checkProxy(String url, Integer port){
		Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
	}
	
	public static void main(String[] args) {
//		System.out.println(new HttpService().checkProxy(host, Integer.parseInt(port)));
		System.out.println(new HttpService().get("http://api.bitkk.com/data/v1/ticker?market=btc_usdt"));
	}
}
