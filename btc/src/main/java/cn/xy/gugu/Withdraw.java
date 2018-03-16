package cn.xy.gugu;

import java.util.Date;
import java.util.Iterator;

import org.apache.http.client.utils.DateUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Withdraw {
	
    public static void main(String[] args){
    	String url = "https://redemption.icowallet.net/api_v2/Merchants/getMerchants";
    	String json = "{\"skip\":0,\"limit\":10,\"orders\""
	      		+ ":[{\"field\":\"withdraw_rate \",\"order\":\"asc\"}],\"where\":"
	      		+ "\"escrow_availabe_amount>withdraw_floorlimit and escrow_availabe_amount!=0 "
	      		+ "and withdraw_pending=false and account_state='accepted'\"}";
    	String result = new GuguHttps().post(url, json,"utf-8");
    	JSONArray rows = JSONObject.parseObject(result).getJSONArray("rows");
    	
    	Iterator it = rows.iterator();
    	StringBuilder sb = new StringBuilder();
    	boolean isSend = false;
    	while(it.hasNext()){
    		JSONObject first = (JSONObject)it.next();
        	String name = first.getString("merchant_name");
        	String nickname = first.getString("merchant_nickname");
        	int rate = first.getInteger("withdraw_rate");
        	if(rate<-8){
        		isSend = true;
        	}
        	sb.append(name).append(",").append(nickname).append(",").append(rate).append("\n");
    	}
    	
    	if(isSend){
            MailSend mail = new MailSend();  
            mail.setSubject("提现卖出");  
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
