package cn.xy.gugu;

import io.swagger.client.model.Msg;
import io.swagger.client.model.MsgContent;
import io.swagger.client.model.UserName;

import java.util.HashMap;
import java.util.Map;

import com.easemob.server.example.api.impl.EasemobSendMessage;
import com.google.gson.GsonBuilder;

public class MsgUtil {
	private static EasemobSendMessage easemobSendMessage = new EasemobSendMessage();
	
    public static void sendText(String message) {
        Msg msg = new Msg();
        MsgContent msgContent = new MsgContent();
        msgContent.type(MsgContent.TypeEnum.TXT).msg(message);
        UserName userName = new UserName();
        userName.add("xy002");
//        Map<String,Object> ext = new HashMap<>();
//        ext.put("test_key","test_value");
        msg.from("xy004").target(userName).targetType("users").msg(msgContent);//.ext(ext);
        System.out.println(new GsonBuilder().create().toJson(msg));
        Object result = easemobSendMessage.sendMessage(msg);
        System.out.println(result);
    }
    
    public static void main(String[] args){
    	new MsgUtil().sendText("你好，测试一下！aaa");
    }
}
