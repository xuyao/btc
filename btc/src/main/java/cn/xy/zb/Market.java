package cn.xy.zb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;

import cn.xy.zb.service.HttpService;
import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.vo.MarketAB;

public class Market {

	public static String[][] arry = null;
	public static Map<String,MarketAB> map = new HashMap<String,MarketAB>();//map
	
	public static void init(String markets){
		
//		String markets = ConstsUtil.getValue("market"); //这个不需要了
		
		String[] marketArr = markets.split(",");
		arry = new String[marketArr.length][];
		for(int i=0;i<marketArr.length;i++) {
			arry[i] = new String[]{marketArr[i]+"_qc", marketArr[i]+"_usdt", marketArr[i]+"_btc"};
		}
		
		
		String path = ConstsUtil.getValue("jsonpath");
		String json ="";
		try {
			json = FileUtils.readFileToString(new File(path), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject jsonObj = JSONObject.parseObject(json);
		
		for(String[] s_arry : arry){
			MarketAB mab_qc = new MarketAB();
			mab_qc.setAmountScale(jsonObj.getJSONObject(s_arry[0]).getInteger("amountScale"));
			mab_qc.setPriceScale(jsonObj.getJSONObject(s_arry[0]).getInteger("priceScale"));
			map.put(s_arry[0], mab_qc);
			
			MarketAB mab_usdt = new MarketAB();
			mab_usdt.setAmountScale(jsonObj.getJSONObject(s_arry[1]).getInteger("amountScale"));
			mab_usdt.setPriceScale(jsonObj.getJSONObject(s_arry[1]).getInteger("priceScale"));
			map.put(s_arry[1], mab_usdt);
		}
		
	}
	
	
	
	public static void main(String[] args){
		HttpService http = new HttpService();
		String json = http.get("http://api.bitkk.com/data/v1/markets");
		String path = ConstsUtil.getValue("jsonpath");
		try {
			FileUtils.writeStringToFile(new File(path), json, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("save json ok!");
	}

}
