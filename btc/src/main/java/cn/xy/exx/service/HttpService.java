package cn.xy.exx.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.springframework.stereotype.Service;

import cn.xy.exx.util.EncryDigestUtil;
import cn.xy.exx.util.HttpUtilManager;
import cn.xy.zb.service.LogService;

import com.zb.kits.MapSort;

@Service
public class HttpService extends LogService{
	
	public final String ACCESS_KEY = "";
	public final String SECRET_KEY = "";
	public final String URL_PREFIX = " https://trade.exxvip.com/api/";// 测试环境,测试环境是ttapi测试不通
	
	/**
	 * 获取json内容(统一加密)
	 * 
	 * @param params
	 * @return
	 */
	public String getJsonPost(Map<String, String> params) {
		params.put("accesskey", ACCESS_KEY);// 这个需要加入签名,放前面
		params.put("nonce", System.currentTimeMillis() + "");

		String sign = EncryDigestUtil.hmacSHA512(MapSort.toStringMap(params), SECRET_KEY); // 参数执行加密
		String method = params.get("method");

		// 加入验证
		params.put("signature", sign);
		
		String json = "";
		try {
			json = HttpUtilManager.getInstance().requestHttpPost(URL_PREFIX, method, params);
		} catch (HttpException | IOException e) {
			e.printStackTrace();
		}
		return json;

	}
	
	
	public String get(String urlAll, Map<String, String> params) {
		if(params!=null){
			params.put("accesskey", ACCESS_KEY);// 这个需要加入签名,放前面
			params.put("nonce", System.currentTimeMillis() + "");
			String signature = EncryDigestUtil.hmacSHA512(MapSort.toStringMap(params), SECRET_KEY);
			urlAll = urlAll+"?"+MapSort.toStringMap(params)+"&signature="+signature;
		}
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
