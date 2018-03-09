package cn.xy.gugu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Recharge {
    
    public static void main(String[] args){
    	String url = "https://redemption.icowallet.net/api_v2/Merchants/getMerchants";
    	String json = "{\"skip\":0,\"limit\":10,\"orders\":[{\"field\":\"recharge_rate \",\"order\""
    			+ ":\"asc\"}],\"where\":\"escrow_availabe_amount>recharge_floorlimit and escrow_availabe_"
    			+ "amount!=0 and recharge_pending=false and account_state='accepted'\"}";
    	
    	String result = new GuguHttps().post(url, json,"utf-8");
    	
    	JSONArray rows = JSONObject.parseObject(result).getJSONArray("rows");
    	
    	Iterator it = rows.iterator();
    	while(it.hasNext()){
    		JSONObject first = (JSONObject)it.next();
        	String name = first.getString("merchant_name");
        	String nickname = first.getString("merchant_nickname");
        	int rate = first.getInteger("recharge_rate");
        	if(rate<0){//如果充值手续费是负数
        		
        	}
        	System.out.println(name + "," + nickname + "," +rate);
    	}
    	
    	
    }
}
