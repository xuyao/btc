package cn.xy.exx.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class EncryDigestUtil {

    public static String hmacSHA512(String data,String key) {
        String result = "";
        byte[] bytesKey = key.getBytes();
        final SecretKeySpec secretKey = new SecretKeySpec(bytesKey, "HmacSHA512");
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKey);
            final byte[] macData = mac.doFinal(data.getBytes());
            byte[] hex = new Hex().encode(macData);
            result = new String(hex, "utf-8");
        } catch (NoSuchAlgorithmException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return result;
    } 
	
	public static void main(String[] args) {
//		System.out.println(digest("mima123465"));
		System.out.println(hmacSHA512("accesskey=yourAccessKey&nonce=1234567890123", "yourSecretKey"));
	}
}
