package cn.xy.zb.service;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.zb.util.NumberUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plato.common.cache.memcached.MemcachedCache;

@Service
public class HLService extends LogService{

	MemcachedCache memcachedClient = MemcacheFactory.getClient();
	Integer size = Integer.parseInt(MemcacheFactory.size);
	String type = MemcacheFactory.type;

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
		logger.info("hl.");
	}
	
}
