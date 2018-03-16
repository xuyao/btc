package cn.xy.gugu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.utils.DateUtils;

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
    	StringBuilder sb = new StringBuilder();
    	Iterator it = rows.iterator();
    	boolean isSend = false;
    	while(it.hasNext()){
    		JSONObject first = (JSONObject)it.next();
        	String name = first.getString("merchant_name");
        	String nickname = first.getString("merchant_nickname");
        	int rate = first.getInteger("recharge_rate");
        	if(rate<-2){
        		isSend = true;
        	}
        	sb.append(name).append(",").append(nickname).append(",").append(rate).append("\n");
    	}
    	
    	if(isSend){
            MailSend mail = new MailSend();  
            mail.setSubject("充值买入");  
            mail.setContent(sb.toString());  
            mail.setTo(new String[] {"58394322@qq.com"});  
            //发送附件列表 可以写绝对路径 也可以写相对路径(起点是项目根目录)  
            //发送邮件  
            try {  
                mail.sendMessage();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }
    	}
    	
    	System.out.print("================="+DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
    	System.out.println(sb.toString());
    }
}
