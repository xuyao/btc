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

	public static String[][] arry = new String[2][];
	public static Map<String,MarketAB> map = new HashMap<String,MarketAB>();//map
	
	public static void init(){
		
		arry[0] = new String[]{"cdc_qc","cdc_usdt","cdc_btc"};
		arry[1] = new String[]{"ddm_qc","ddm_usdt","ddm_btc"};
		
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
		String json = http.get("http://api.zb.com/data/v1/markets");
		String path = ConstsUtil.getValue("jsonpath");
		try {
			FileUtils.writeStringToFile(new File(path), json, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("save json ok!");
	}

}
