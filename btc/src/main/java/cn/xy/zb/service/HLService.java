package cn.xy.zb.service;

import java.io.IOException;
import java.util.Iterator;

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
				String totalOld = (String)memcachedClient.get("total");
				
				String son = (String)memcachedClient.get("on");
				if(total.compareTo("1600000000")<0) {
					if(total.compareTo(totalOld)>0 && "t".equals(son)) {//如果新的交易量大于上一个,且上一个on是t
						memcachedClient.set("on", "t");
					}else {//如果交易量一直下滑
						memcachedClient.set("on", "f");
					}
				}else {
					if(total.compareTo(totalOld)>=0 || "t".equals(son)) {//如果新的交易量大于上一个
						memcachedClient.set("on", "t");
					}else {//如果交易量一直下滑
						memcachedClient.set("on", "f");
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
