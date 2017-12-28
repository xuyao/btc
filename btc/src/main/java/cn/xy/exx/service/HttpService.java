package cn.xy.exx.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpException;
import org.springframework.stereotype.Service;

import cn.xy.exx.util.EncryDigestUtil;
import cn.xy.exx.util.HttpUtilManager;

import com.zb.kits.MapSort;

@Service
public class HttpService {
	
	public final String ACCESS_KEY = "2e90b0cd-c69a-41dc-b190-2bb586515cf8";
	public final String SECRET_KEY = "2e7527ddd21d19f7aea9a168f6609a486883dc61";
	public final String URL_PREFIX = " https://trade.exx.com/api/";// 测试环境,测试环境是ttapi测试不通
	
	/**
	 * 获取json内容(统一加密)
	 * 
	 * @param params
	 * @return
	 */
	public String getJsonPost(Map<String, String> params) {
		String urlbase = "accesskey="+ACCESS_KEY+"&nonce="+System. currentTimeMillis();
//		String baseURL = "https://trade.exx.com/api/getBalance";
		String signature = EncryDigestUtil.hmacSHA512(urlbase, SECRET_KEY);
		String url = URL_PREFIX +params.get("method")+ "?" + urlbase + "&signature=" + signature;
		String json = "";
		try {
			json = HttpUtilManager.getInstance().requestHttpPost(url, params);
			System.out.println(json);
		} catch (HttpException | IOException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	
	public String get(String urlAll) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";// 模拟浏览器
		try {
			URL url = new URL(urlAll);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
