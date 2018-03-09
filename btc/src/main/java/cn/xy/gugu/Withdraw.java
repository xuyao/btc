package cn.xy.gugu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;

import cn.xy.zb.service.HttpService;

import com.zb.kits.HttpUtilManager;

public class Withdraw {
	
    public static final String APP_ID = "wx1587a095c5c9bb77";
    public static final String APP_SECRET = "6ae12d5e44a513c1f6aae904e83132c7";
    public static final String ACCOUNT_ID = "gh_7c5e85ae78d5";//公众号微信号
    public static final String TOKEN = "7_h4_Y9qqWMOVaDm-rCor-mx_zxZTkMLmLS2CmObFuQhINiKC-g8kz6yCVSR2n2Xq3lfxDTpNWsIHg6paaUYrJ67a8f_29BCsQ0ibOR"
    		+ "x6sWSD13GanccH9adjDq6pNz9D5KInBdt1e8gClS_SiBSXeAGASJO";
    public static final String OPENID = "oFgvrjo2wCRZN7b3RsWhjdaHuOr4"; //某粉丝的openid
    
    public static void main(String[] args){
//    	String url = "https://redemption.icowallet.net/api_v2/Merchants/getMerchants";
//    	String json = "{\"skip\":0,\"limit\":10,\"orders\""
//	      		+ ":[{\"field\":\"withdraw_rate \",\"order\":\"asc\"}],\"where\":"
//	      		+ "\"escrow_availabe_amount>withdraw_floorlimit and escrow_availabe_amount!=0 "
//	      		+ "and withdraw_pending=false and account_state='accepted'\"}";
//    	String result = new GuguHttps().post(url, json,"utf-8");
//    	JSONArray rows = JSONObject.parseObject(result).getJSONArray("rows");
//    	
//    	Iterator it = rows.iterator();
//    	while(it.hasNext()){
//    		JSONObject first = (JSONObject)it.next();
//        	String name = first.getString("merchant_name");
//        	String nickname = first.getString("merchant_nickname");
//        	int rate = first.getInteger("withdraw_rate");
//        	System.out.println(name + "," + nickname + "," +rate);
//    	}
    	String content="{\"filter\":{\"is_to_all\":false,\"tag_id\":2},\"text\":"
    			+ "{\"content\":\"徐尧可能是天才！\"},\"msgtype\":\"text\"}";
//       BaseResponse result = MassMsgApi.sendMassMessageToAll(TOKEN, content);
		String result = new GuguHttps().post("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+TOKEN, 
				content,"utf-8");
    	System.out.println("sendMassMessageToAll: " + result);

    }
}
