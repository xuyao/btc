package cn.xy.btc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.btc.vo.AskBid;
import cn.xy.btc.vo.Deal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

@Service
public class CompService {

	@Autowired
	HttpService httpService;
	
	//得到挂单的买卖价格和数量
	public AskBid getAskBid(String market, Double cnyUsd){
		String ha = "http://api.zb.com/data/v1/depth?market="+market+"&size=2";
		String result = httpService.get(ha);
//		System.out.println(result);
		JSONArray asksArr = JSON.parseObject(result).getJSONArray("asks");
		JSONArray bidsArr = JSON.parseObject(result).getJSONArray("bids");
		JSONArray asks1 = asksArr.getJSONArray(0);
		JSONArray bids1 = bidsArr.getJSONArray(0);
		
		JSONArray asks2 = asksArr.getJSONArray(1);
		JSONArray bids2 = bidsArr.getJSONArray(1);
		
		AskBid ab = new AskBid();
		ab.setAsk1(asks1.getDouble(0)*cnyUsd);
		ab.setAsk1_amount(asks1.getInteger(1));
		ab.setAsk2(asks2.getDouble(0)*cnyUsd);
		ab.setAsk2_amount(asks2.getInteger(1));
		ab.setBid1(bids1.getDouble(0)*cnyUsd);
		ab.setBid1_amount(bids1.getInteger(1));
		ab.setBid2(bids2.getDouble(0)*cnyUsd);
		ab.setBid2_amount(bids2.getInteger(1));
		ab.setMarket(market);
		return ab;
	}
	
	
	//得到挂单的买卖价格和数量
	public AskBid getAskBid(String market){
		return getAskBid(market, 1d);
	}
	
	
	//比较两个市场的套利价格,从ab1买，去ab2卖
	public Deal comp(AskBid ab1, AskBid ab2){
		Deal deal = new Deal();
		if(ab2.getBid1()/ab1.getAsk1()>1.012){//如果价格之差大于ab2的1.2%认为有利可图
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
