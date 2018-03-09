package cn.xy.gugu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class GuguHttps {
	/**
     * 
     * @param url   需要请求的网关路径
     * @param sendData  请求时需要传入的参数
     * @param urlencode url的编码格式
     * @param connTimeOut   链接超时时间 
     * @param readTimeOut   读取超时时间
     * @param contentType   请求头部  固定输入"application/x-www-form-urlencoded;charset="+urlencode
     * @param header     输入null
     * @return
     */
    public static String post(String url,String sendData,String urlencode){
        String result = "";
        BufferedReader in = null;
        DataOutputStream out = null;
        int code = 999;
        HttpsURLConnection httpsConn = null;
        HttpURLConnection httpConn = null;
        try{
            URL myURL = new URL(url);
            System.out.println("请求地址："+url);
            if(url.startsWith("https://")){
                httpsConn =    (HttpsURLConnection) myURL.openConnection();
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                            public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                            }
                            public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }
                    };
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                httpsConn.setSSLSocketFactory(sc.getSocketFactory());
                HostnameVerifier hv = new HostnameVerifier() {
                    @Override
                    public boolean verify(String urlHostName, SSLSession session) {
                        return true;
                    }
                }; 
                httpsConn.setHostnameVerifier(hv);
                    
                httpsConn.setRequestProperty("Accept-Charset", urlencode);
                httpsConn.setRequestProperty("User-Agent","java HttpsURLConnection");
//                if(header!=null){
//                    for(String key:header.keySet()){
//                        httpsConn.setRequestProperty(key, (String)header.get(key));
//                    }
//                }
                httpsConn.setRequestMethod("POST");
                httpsConn.setUseCaches(false);
                httpsConn.setRequestProperty("Content-Type","application/json"); 
                httpsConn.setConnectTimeout(30000);
                httpsConn.setReadTimeout(30000);
                httpsConn.setDoInput(true);
                httpsConn.setInstanceFollowRedirects(true); 
                if(sendData !=null){
                    httpsConn.setDoOutput(true);
                    // 获取URLConnection对象对应的输出流
                    out = new DataOutputStream(httpsConn.getOutputStream());
                    // 发送请求参数
                    out.write(sendData.getBytes(urlencode));
                    // flush输出流的缓冲
                    out.flush();
                    out.close();
                }
                // 取得该连接的输入流，以读取响应内容
                in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(),urlencode));
                code = httpsConn.getResponseCode();
            }else{
                httpConn =    (HttpURLConnection) myURL.openConnection();
                httpConn.setRequestProperty("Accept-Charset", urlencode);
                httpConn.setRequestProperty("user-agent","java HttpURLConnection");
//                if(header!=null){
//                    for(String key:header.keySet()){
//                        httpConn.setRequestProperty(key, (String)header.get(key));
//                    }
//                }
                httpConn.setRequestMethod("POST");
                httpConn.setUseCaches(false);
                httpConn.setRequestProperty("Content-Type","application/json"); 
                httpConn.setConnectTimeout(30000);
                httpConn.setReadTimeout(30000);
                httpConn.setDoInput(true);
                httpConn.setInstanceFollowRedirects(true); 
                if(sendData !=null){
                    httpConn.setDoOutput(true);
                    // 获取URLConnection对象对应的输出流
                    out = new DataOutputStream(httpConn.getOutputStream());
                    // 发送请求参数
                    out.write(sendData.getBytes(urlencode));
                    // flush输出流的缓冲
                    out.flush();
                    out.close();
                }
                // 取得该连接的输入流，以读取响应内容
                in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),urlencode));
                code = httpConn.getResponseCode();
            }
            if (HttpURLConnection.HTTP_OK == code){ 
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
//                    System.out.println("=====反回结果====="+ line);
                }
                System.out.println(result);
            }else{
                result = null;
                throw new Exception("支付失败,服务端响应码："+code);
            }
        }catch(IOException e){
        	e.printStackTrace();
            System.out.println("请求地址："+url);
            result = null;
        }catch(Exception e){
        	e.printStackTrace();
            result = null;
        }finally{
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if(httpConn!=null){
                httpConn.disconnect();
            }
            if(httpsConn!=null){
                httpsConn.disconnect();
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }
    
}
