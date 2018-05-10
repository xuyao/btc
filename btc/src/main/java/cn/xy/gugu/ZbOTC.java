package cn.xy.gugu;

import java.io.IOException;
import java.util.Date;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ZbOTC {
    
    public static void main(String[] args){
    	
    	Document doc = null;
		try {
			doc = Jsoup.connect("https://vip.bitkk.com/otc/trade/qc_cny?type=2").get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Elements tbodys = doc.getElementsByTag("tbody");
    	Element tbody = tbodys.get(2);
    	Elements price = tbody.getElementsByClass("price");
    	String[] arr = price.get(0).text().split(" ");
    	double d = Double.parseDouble(arr[0]);
    	System.out.println(d);
    	
    	if(d<0.99){
            MailSend mail = new MailSend();  
            mail.setSubject("zb充值买入符合条件");  
            mail.setContent("otc买入符合条件，赶紧下手");  
            mail.setTo(new String[] {"58394322@qq.com"});  
            //发送附件列表 可以写绝对路径 也可以写相对路径(起点是项目根目录)  
            //发送邮件  
            try {  
                mail.sendMessage();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }
    	}
    	
    	if(d>0.995){
            MailSend mail = new MailSend();  
            mail.setSubject("zb卖出符合条件");  
            mail.setContent("otc赶紧卖，赶紧下手");  
            mail.setTo(new String[] {"58394322@qq.com"});  
            //发送附件列表 可以写绝对路径 也可以写相对路径(起点是项目根目录)  
            //发送邮件  
            try {  
                mail.sendMessage();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }
    	}
    }
}
