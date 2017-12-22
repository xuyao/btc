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

	public static String[][] arry = new String[18][];
	public static Map<String,MarketAB> map = new HashMap<String,MarketAB>();//map
	
	public static void init(){
		
		arry[0] = new String[]{"btc_qc","btc_usdt",null};
		arry[1] = new String[]{"bcc_qc","bcc_usdt","bcc_btc"};
		arry[2] = new String[]{"ubtc_qc","ubtc_usdt","ubtc_btc"};
		arry[3] = new String[]{"ltc_qc","ltc_usdt","ltc_btc"};
		arry[4] = new String[]{"eth_qc","eth_usdt","eth_btc"};
		arry[5] = new String[]{"etc_qc","etc_usdt","etc_btc"};
		arry[6] = new String[]{"eos_qc","eos_usdt","eos_btc"};
		arry[7] = new String[]{"qtum_qc","qtum_usdt","qtum_btc"};
		arry[8] = new String[]{"hsr_qc","hsr_usdt","hsr_btc"};
		arry[9] = new String[]{"xrp_qc","xrp_usdt","xrp_btc"};
		arry[10] = new String[]{"bcd_qc","bcd_usdt","bcd_btc"};
		arry[11] = new String[]{"dash_qc","dash_usdt","dash_btc"};
		arry[12] = new String[]{"sbtc_qc","sbtc_usdt","sbtc_btc"};
		arry[13] = new String[]{"ink_qc","ink_usdt","ink_btc"};
		arry[14] = new String[]{"tv_qc","tv_usdt","tv_btc"};
		arry[15] = new String[]{"bcx_qc","bcx_usdt","bcx_btc"};
		arry[16] = new String[]{"bth_qc","bth_usdt","bth_btc"};
		arry[17] = new String[]{"lbtc_qc","lbtc_usdt","lbtc_btc"};
//		arry[19] = new String[]{"bts_qc","bts_usdt","bts_btc"};
		
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
