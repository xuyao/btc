package cn.xy.zb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plato.common.cache.memcached.MemcachedCache;

import cn.xy.zb.service.HttpService;
import cn.xy.zb.service.MemcacheFactory;
import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.util.NumberUtil;

public class Ma {
	
	//market,tax
	public static Map<String,Double> map = new HashMap<String,Double>();//map
	
	static MemcachedCache memcachedClient = MemcacheFactory.getClient();
	
	public static void main(String[] args){
			String type = "5min";
			String size = "144";
			HttpService http = new HttpService();
			String json = http.get("http://api.bitkk.com/data/v1/kline?market=usdt_qc&type="+type+"&size="+size);
			JSONObject jsonObj = JSONObject.parseObject(json);
			JSONArray jsArr = jsonObj.getJSONArray("data");
			Iterator it = jsArr.listIterator();
			double ma = 0;
			int i=0;
			while(it.hasNext()){
				JSONArray jsa = (JSONArray)it.next();
				ma = ma+jsa.getDoubleValue(4);
				i++;
			}
				
		    ma =NumberUtil.formatDoubleHP(ma/i, 4);
		    memcachedClient.set("ma", ma);
		    System.out.println(i+" "+ma);
		}
	
}
