package cn.xy.btc.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.xy.btc.vo.AccountInfo;
import cn.xy.btc.vo.AskBid;
import cn.xy.btc.vo.Deal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class CompService {

	@Autowired
	HttpService httpService;
	
	//获得用户信息
	public AccountInfo getAccountInfo(){
		AccountInfo ai = null;
		try {
			// 需加密的请求参数
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "getAccountInfo");
			String json = httpService.getJsonPost(params);
			System.out.println(json);
			JSONArray jsonArry = JSON.parseObject(json).getJSONObject("result").getJSONArray("coins");
			
			ai = new AccountInfo();
			JSONObject jsonObj = jsonArry.getJSONObject(0);
			if("qc".equalsIgnoreCase(jsonObj.getString("key")))
					ai.setQcAvailable(jsonObj.getDouble("available"));
			jsonObj = jsonArry.getJSONObject(1);
			if("usdt".equalsIgnoreCase(jsonObj.getString("key")))
				ai.setUsdtAvailable(jsonObj.getDouble("available"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ai;
	}
	
	
	//得到挂单的买卖价格和数量
	public AskBid getAskBid(String market){
		String ha = "http://api.zb.com/data/v1/depth?market="+market+"&size=2";
		String result = httpService.get(ha);
		if(StringUtils.isEmpty(result))//如果行情没取到直接返回
			return null;
//		System.out.println(result);
		JSONArray asksArr = JSON.parseObject(result).getJSONArray("asks");
		JSONArray bidsArr = JSON.parseObject(result).getJSONArray("bids");
		JSONArray asks1 = asksArr.getJSONArray(0);
		JSONArray bids1 = bidsArr.getJSONArray(0);
		
		JSONArray asks2 = asksArr.getJSONArray(1);
		JSONArray bids2 = bidsArr.getJSONArray(1);
		
		AskBid ab = new AskBid();
		ab.setAsk1(asks1.getDouble(0));
		ab.setAsk1_amount(asks1.getDouble(1));
		ab.setAsk2(asks2.getDouble(0));
		ab.setAsk2_amount(asks2.getDouble(1));
		ab.setBid1(bids1.getDouble(0));
		ab.setBid1_amount(bids1.getDouble(1));
		ab.setBid2(bids2.getDouble(0));
		ab.setBid2_amount(bids2.getDouble(1));
		ab.setMarket(market);
		return ab;
	}
	
	
	//比较两个市场的套利价格,从ab1买，去ab2卖，第一个参数是人民币，第二个参数是usd
	public Deal compCnyUsd(AskBid ab1, AskBid ab2 ,Double usd_cny){
		Deal deal = new Deal();
		if((ab2.getBid1()*usd_cny)/ab1.getAsk1()>1.012){//如果价格之差大于ab2的1.2%认为有利可图
			deal.setBuyPrice(ab1.getAsk1());//买入价格设置为ab1的卖一价格
			deal.setSellPrice(ab2.getBid1());//卖出价格设置为ab2的买一价格
			deal.setBuyMarket(ab1.getMarket());
			
			deal.setBuyAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//量取小的那个
			deal.setSellAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//同上，买卖量相同
			deal.setSellMarket(ab2.getMarket());
		}else{
			deal = null;
		}
		return deal;
	}

	//第一个参数是usd，第二个参数是人民币
	public Deal compUsdCny(AskBid ab1, AskBid ab2 ,Double usd_cny){
		Deal deal = new Deal();
		if(ab2.getBid1()/(ab1.getAsk1()*usd_cny)>1.012){//如果价格之差大于ab2的1.2%认为有利可图
			deal.setBuyPrice(ab1.getAsk1());//买入价格设置为ab1的卖一价格
			deal.setSellPrice(ab2.getBid1());//卖出价格设置为ab2的买一价格
			deal.setBuyMarket(ab1.getMarket());
			
			deal.setBuyAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//量取小的那个
			deal.setSellAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//同上，买卖量相同
			deal.setSellMarket(ab2.getMarket());
		}else{
			deal = null;
		}
		return deal;
	}
	
	

	public HttpService getHttpService() {
		return httpService;
	}


	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
	
	
}
