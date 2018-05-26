package cn.xy.zb.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plato.common.cache.memcached.MemcachedCache;

import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.vo.AccountInfo;
import cn.xy.zb.vo.AskBid;
import cn.xy.zb.vo.Deal;
import cn.xy.zb.vo.Ticker;

@Service
public class CompService extends LogService{

	@Autowired
	HttpService httpService;
	MemcachedCache memcachedClient = MemcacheFactory.getClient();
	
	public Double usd_cny = (Double)memcachedClient.get("hl");//汇率
	Double comp_cny_usd = ConstsUtil.getCompCnyUsd();//人民币比美元
	Double comp_usd_cny = ConstsUtil.getCompUsdCny();//美元比人民币
	
	
	//获得用户信息
	public AccountInfo getAccountInfo(){
		AccountInfo ai = null;
		try {
			// 需加密的请求参数
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "getAccountInfo");
			String json = httpService.getJsonPost(params);
			JSONObject result = JSON.parseObject(json);
			if(result == null)
				return null;
			result = result.getJSONObject("result");
			JSONArray jsonArry = result.getJSONArray("coins");
			
			ai = new AccountInfo();
			Iterator it = jsonArry.iterator();
			while(it.hasNext()) {
				JSONObject jsonObj = (JSONObject)it.next();
				if("qc".equalsIgnoreCase(jsonObj.getString("key"))){
					ai.setQcAvailable(jsonObj.getDouble("available"));
					ai.setQcfreez(jsonObj.getDouble("freez"));
					
				}
				if("usdt".equalsIgnoreCase(jsonObj.getString("key"))){
					ai.setUsdtAvailable(jsonObj.getDouble("available"));
					ai.setUsdtfreez(jsonObj.getDouble("freez"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ai;
	}
	
	
	//得到挂单的买卖价格和数量
	public AskBid getAskBid(String market){
		String ha = "http://api.bitkk.com/data/v1/depth?market="+market+"&size=2";
		String result = httpService.get(ha);
//		System.out.println(market+result);
		if(StringUtils.isEmpty(result))//如果行情没取到直接返回
			return null;

		JSONArray asksArr = JSON.parseObject(result).getJSONArray("asks");
		JSONArray bidsArr = JSON.parseObject(result).getJSONArray("bids");
		if(asksArr==null || bidsArr==null
				||asksArr.size()<2 ||bidsArr.size()<2)
			return null;
		
		JSONArray asks1 = asksArr.getJSONArray(0);
		JSONArray bids1 = bidsArr.getJSONArray(0);
		
		JSONArray asks2 = asksArr.getJSONArray(1);
		JSONArray bids2 = bidsArr.getJSONArray(1);
		
		AskBid ab = new AskBid();
		ab.setAsk1(asks1.getDouble(0));//卖二
		ab.setAsk1_amount(asks1.getDouble(1));
		ab.setAsk2(asks2.getDouble(0));//卖一
		ab.setAsk2_amount(asks2.getDouble(1));
		ab.setBid1(bids1.getDouble(0));//买一
		ab.setBid1_amount(bids1.getDouble(1));
		ab.setBid2(bids2.getDouble(0));//买二
		ab.setBid2_amount(bids2.getDouble(1));
		ab.setMarket(market);
		return ab;
	}
	
	
	//嗅探
	public Double sniffCnyUsd(AskBid ab1, AskBid ab2){
		Double d = (ab2.getBid1()*usd_cny)/ab1.getAsk2();
		return d;
	}
	
	//嗅探
	public Double sniffUsdCny(AskBid ab1, AskBid ab2){
		Double d = ab2.getBid1()/(ab1.getAsk2()*usd_cny);
		return d;
	}
	
	//比较两个市场的套利价格,从ab1买，去ab2卖，第一个参数是人民币，第二个参数是usd
	public Deal compCnyUsd(AskBid ab1, AskBid ab2){
		Deal deal =null;
		
//		if((ab2.getBid1()*usd_cny)/ab1.getAsk1()>comp_cny_usd){//买一和卖二比价
//			deal = new Deal();
//			deal.setBuyPrice(ab1.getAsk1());//买入价格设置为ab1的卖一价格
//			deal.setSellPrice(ab2.getBid1());//卖出价格设置为ab2的买一价格
//			deal.setBuyMarket(ab1.getMarket());
//			
//			deal.setBuyAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//量取小的那个
//			deal.setSellAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//同上，买卖量相同
//			deal.setSellMarket(ab2.getMarket());
//			return deal;
//		}
		
		if((ab2.getBid2()*usd_cny)/ab1.getAsk2()>comp_cny_usd){//买二和卖一比价
			deal = new Deal();
			deal.setBuyPrice(ab1.getAsk2());//买入价格设置为ab1的卖一价格
			deal.setSellPrice(ab2.getBid2());//卖出价格设置为ab2的买一价格
			deal.setBuyMarket(ab1.getMarket());
			
			deal.setBuyAmount(Math.min(ab1.getAsk2_amount(), ab2.getBid2_amount()));//量取小的那个
			deal.setSellAmount(Math.min(ab1.getAsk2_amount(), ab2.getBid2_amount()));//同上，买卖量相同
			deal.setSellMarket(ab2.getMarket());
			return deal;
		}
		
//		if((ab2.getBid1()*usd_cny)/ab1.getAsk2()>comp_cny_usd){//买一和卖一比价
//			deal = new Deal();
//			deal.setBuyPrice(ab1.getAsk2());//买入价格设置为ab1的卖一价格
//			deal.setSellPrice(ab2.getBid1());//卖出价格设置为ab2的买一价格
//			deal.setBuyMarket(ab1.getMarket());
//			
//			deal.setBuyAmount(Math.min(ab1.getAsk2_amount(), ab2.getBid1_amount()));//量取小的那个
//			deal.setSellAmount(Math.min(ab1.getAsk2_amount(), ab2.getBid1_amount()));//同上，买卖量相同
//			deal.setSellMarket(ab2.getMarket());
//			return deal;
//		}
		return deal;
	}
	

	//第一个参数是usd，第二个参数是人民币
	public Deal compUsdCny(AskBid ab1, AskBid ab2){
		Deal deal =null;
		
//		if(ab2.getBid1()/(ab1.getAsk1()*usd_cny)>comp_usd_cny){//买一对卖二
//			deal = new Deal();
//			deal.setBuyPrice(ab1.getAsk1());//买入价格设置为ab1的卖一价格
//			deal.setSellPrice(ab2.getBid1());//卖出价格设置为ab2的买一价格
//			deal.setBuyMarket(ab1.getMarket());
//			
//			deal.setBuyAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//量取小的那个
//			deal.setSellAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//同上，买卖量相同
//			deal.setSellMarket(ab2.getMarket());
//			return deal;
//		}
		
		if(ab2.getBid2()/(ab1.getAsk2()*usd_cny)>comp_usd_cny){//买二对卖一
			deal = new Deal();
			deal.setBuyPrice(ab1.getAsk2());//买入价格设置为ab1的卖一价格
			deal.setSellPrice(ab2.getBid2());//卖出价格设置为ab2的买一价格
			deal.setBuyMarket(ab1.getMarket());
			
			deal.setBuyAmount(Math.min(ab1.getAsk2_amount(), ab2.getBid2_amount()));//量取小的那个
			deal.setSellAmount(Math.min(ab1.getAsk2_amount(), ab2.getBid2_amount()));//同上，买卖量相同
			deal.setSellMarket(ab2.getMarket());
			return deal;
		}
		
//		if(ab2.getBid1()/(ab1.getAsk2()*usd_cny)>comp_usd_cny){//买一对卖一
//			deal = new Deal();
//			deal.setBuyPrice(ab1.getAsk2());//买入价格设置为ab1的卖一价格
//			deal.setSellPrice(ab2.getBid1());//卖出价格设置为ab2的买一价格
//			deal.setBuyMarket(ab1.getMarket());
//			
//			deal.setBuyAmount(Math.min(ab1.getAsk2_amount(), ab2.getBid1_amount()));//量取小的那个
//			deal.setSellAmount(Math.min(ab1.getAsk2_amount(), ab2.getBid1_amount()));//同上，买卖量相同
//			deal.setSellMarket(ab2.getMarket());
//			return deal;
//		}
		return deal;
	}
	
	
	public Ticker getTicker(String currency) {
		Ticker ticker = null;
		try {
			// 请求地址
			String url = "http://api.bitkk.com/data/v1/ticker?market=" + currency;
			String result = httpService.get(url);
			JSONObject jsonObj = JSONObject.parseObject(result);
			jsonObj = jsonObj.getJSONObject("ticker");
			ticker = new Ticker();
			ticker.setBuy(jsonObj.getDouble("buy"));
			ticker.setHigh(jsonObj.getDouble("high"));
			ticker.setLast(jsonObj.getDouble("last"));
			ticker.setLow(jsonObj.getDouble("low"));
			ticker.setSell(jsonObj.getDouble("sell"));
			ticker.setVol(jsonObj.getDouble("vol"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ticker;
	}
	

	public HttpService getHttpService() {
		return httpService;
	}


	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
	
	
}
