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

	public static String[][] arry = new String[16][];
	public static Map<String,MarketAB> map = new HashMap<String,MarketAB>();//map
	
	public static void init(){
		
		arry[0] = new String[]{"bcx_qc","bcx_usdt","bcx_btc"};
		arry[1] = new String[]{"bth_qc","bth_usdt","bth_btc"};
		arry[2] = new String[]{"lbtc_qc","lbtc_usdt","lbtc_btc"};
		arry[3] = new String[]{"eos_qc","eos_usdt","eos_btc"};
		arry[4] = new String[]{"chat_qc","chat_usdt","chat_btc"};
		arry[5] = new String[]{"hlc_qc","hlc_usdt","hlc_btc"};
		arry[6] = new String[]{"bcw_qc","bcw_usdt","bcw_btc"};
		arry[7] = new String[]{"btp_qc","btp_usdt","btp_btc"};
		arry[8] = new String[]{"bat_qc","bat_usdt","bat_btc"};
		arry[9] = new String[]{"1st_qc","1st_usdt","1st_btc"};
		arry[10] = new String[]{"safe_qc","safe_usdt","safe_btc"};
		arry[11] = new String[]{"ent_qc","ent_usdt","ent_btc"};
		arry[12] = new String[]{"topc_qc","topc_usdt","topc_btc"};
		arry[13] = new String[]{"btn_qc","btn_usdt","btn_btc"};
		arry[14] = new String[]{"qun_qc","qun_usdt","qun_btc"};
		arry[15] = new String[]{"true_qc","true_usdt","true_btc"};
		
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
