package cn.xy.zb.service;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.util.NumberUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plato.common.cache.memcached.MemcachedCache;

@Service
public class HLService extends LogService{

	MemcachedCache memcachedClient = MemcacheFactory.getClient();
	Integer size = Integer.parseInt(MemcacheFactory.size);
	String type = MemcacheFactory.type;
	String on = ConstsUtil.getValue("on");//人民币比美元

	@Autowired
	HttpService httpService;
	
	public void work(){
		String json = httpService.get("http://api.zb.com/data/v1/kline?market=usdt_qc&type="+type+"&size="+size);
		JSONObject jsonObj = JSONObject.parseObject(json);
		JSONArray jsArr = jsonObj.getJSONArray("data");
		Iterator it = jsArr.listIterator();
		Double ma = 0d;
		while(it.hasNext()){
			JSONArray jsa = (JSONArray)it.next();
			ma = ma+jsa.getDoubleValue(4);
		}
		memcachedClient.set("hl", NumberUtil.formatDoubleHP(ma/size, 4));
		
		if("r".equals(on)){
			try {
				Document doc = Jsoup.connect("https://www.feixiaohao.com/exchange/zb/").get();
				Elements container = doc.getElementsByClass("num");
				Element e = container.get(3);
				String s = e.text();
				String[] arr = s.split(" ");
				String total = arr[0].replaceAll("¥", "");
				total = total.replaceAll(",", "");
				String k1 = (String)memcachedClient.get("k1");
				String k2 = (String)memcachedClient.get("k2");
				
				if(StringUtils.isEmpty(k1)) {
					memcachedClient.set("k1", total);
					k1 = total;
				}

				if(StringUtils.isEmpty(k2)) {
					memcachedClient.set("k2", total);
					k2 = total;
				}
					

				if(total.compareTo("1600000000")<0) {
					if(total.compareTo(k2)>0) {
						if(k2.compareTo(k1)>0) {//如果k2大于k1
							memcachedClient.set("on", "t");//打开开关,然后往前移1位
							memcachedClient.set("k1", k2);
							memcachedClient.set("k2", total);
						}else {//如果k2小于k1
							memcachedClient.set("on", "f");//打开开关,然后往前移1位
							memcachedClient.set("k1", k2);
							memcachedClient.set("k2", total);
						}
					}else if(total.compareTo(k2) == 0){
						//do noting, but not set k1 and k2
					}else {
						memcachedClient.set("on", "f");//打开开关,然后往前移1位
						memcachedClient.set("k1", k2);
						memcachedClient.set("k2", total);
					}
				}else if(total.compareTo("1600000000")>=0 && total.compareTo("2000000000")<=0){//交易额在1600000000-1900000000
					if(total.compareTo(k2)>0) {
						memcachedClient.set("on", "t");//打开开关,然后往前移1位
						memcachedClient.set("k1", k2);
						memcachedClient.set("k2", total);
					}else if(total.compareTo(k2) == 0){
						//do noting, but not set k1 and k2
					}else {
						memcachedClient.set("on", "f");//打开开关,然后往前移1位
						memcachedClient.set("k1", k2);
						memcachedClient.set("k2", total);
					}
					
				}else {//足量搞
					if(total.compareTo(k2)<0) {
						if(k2.compareTo(k1)<0) {
							memcachedClient.set("on", "f");
							memcachedClient.set("k1", k2);
							memcachedClient.set("k2", total);
						}else {//如果k2小于k1
							memcachedClient.set("on", "t");//打开开关,然后往前移1位
							memcachedClient.set("k1", k2);
							memcachedClient.set("k2", total);
						}
					}else if(total.compareTo(k2) == 0){
						//do noting, but not set k1 and k2
					}else {
						memcachedClient.set("on", "t");//打开开关,然后往前移1位
						memcachedClient.set("k1", k2);
						memcachedClient.set("k2", total);
					}
				}
				memcachedClient.set("total", total);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else if("t".equals(on)){
			memcachedClient.set("on", "t");
		}else{
			memcachedClient.set("on", "f");
		}

		logger.info("hl.");
	}
	
}
