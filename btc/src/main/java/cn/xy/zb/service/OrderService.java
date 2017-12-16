package cn.xy.zb.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.xy.zb.Market;
import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.util.DateUtil;
import cn.xy.zb.util.NumberUtil;
import cn.xy.zb.vo.AccountInfo;
import cn.xy.zb.vo.AskBid;
import cn.xy.zb.vo.Deal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class OrderService {

	
	@Autowired
	HttpService httpService;
	
	Double usd_cny = ConstsUtil.getCnyUsd();//汇率
	Double qc_limit = ConstsUtil.getQcLimit();//qc限制
	
	//qc_usdt
	public void dealQc2Usdt(Deal deal){
		if(deal==null)
			return;
		
		Double buyPrice = deal.getBuyPrice();
		Double sellPrice = deal.getSellPrice();
		sellPrice = sellPrice*usd_cny;
		System.out.println("*****************************************");

		Double amount = qc_limit/buyPrice;//qc的价格折算的数量
		if(amount>deal.getBuyAmount())//如果限制仓位下的amount小于挂单买入的amount，以小的为准
			amount=deal.getBuyAmount();
		amount =  getAmount(deal.getBuyMarket(), amount);//买入量按照市场进行小数点转换
		
		StringBuilder sb = new StringBuilder(DateUtil.formatLongPattern(new Date())).append("\n");
		sb.append(" 市场：").append(deal.getBuyMarket());
		sb.append(" ").append(deal.getBuyPrice()).append(" ").append(buyPrice);
		sb.append(" ").append(deal.getBuyAmount());
		sb.append("\n");
		sb.append(" 市场：").append(deal.getSellMarket());
		sb.append(" ").append(deal.getSellPrice()).append(" ").append(sellPrice);;
		sb.append(" ").append(deal.getSellAmount());
		sb.append("\n");
		sb.append(" 买入价格：").append(deal.getBuyPrice()).append(" 卖出价格：").append(deal.getSellPrice()).append(" ").append(sellPrice);
		sb.append(" 数量：").append(amount).append(" 利：").append((sellPrice-buyPrice)*amount*0.998);//手续费
		System.out.println(sb.toString());
	}
	
	
	//usdt_qc
	public void dealUsdt2Qc(Deal deal){
		if(deal==null)
			return;
		Double buyPrice = deal.getBuyPrice();
		Double sellPrice = deal.getSellPrice();
		buyPrice = buyPrice*usd_cny;
		System.out.println("*****************************************");

		Double amount = qc_limit/buyPrice;//qc的价格折算的数量
		if(amount>deal.getBuyAmount())//如果限制仓位下的amount小于挂单买入的amount，以小的为准
			amount=deal.getBuyAmount();
		amount =  getAmount(deal.getBuyMarket(), amount);//买入量按照市场进行小数点转换
		
		StringBuilder sb = new StringBuilder(DateUtil.formatLongPattern(new Date())).append("\n");
		sb.append(" 市场：").append(deal.getBuyMarket());
		sb.append(" ").append(deal.getBuyPrice()).append(" ").append(buyPrice);
		sb.append(" ").append(deal.getBuyAmount());
		sb.append("\n");
		sb.append(" 市场：").append(deal.getSellMarket());
		sb.append(" ").append(deal.getSellPrice()).append(" ").append(sellPrice);;
		sb.append(" ").append(deal.getSellAmount());
		sb.append("\n");
		sb.append(" 买入价格：").append(deal.getBuyPrice()).append(" ").append(buyPrice).append(" 卖出价格：").append(deal.getSellPrice());
		sb.append(" 数量：").append(amount).append(" 利：").append((sellPrice-buyPrice)*amount*0.998);//手续费
		System.out.println(sb.toString());
	}
	
	
	//得到相应市场的买入数量
	public Double getAmount(String market, Double amount){
		return NumberUtil.formatDouble(amount, Market.map.get(market).getAmountScale());
	}
	
	
	//下单 tradeType交易类型1/0[buy/sell]
	public void order(String currency, String tradeType,String price, String amount){
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "order");
			params.put("price", price);
			params.put("amount", amount);
			params.put("tradeType", "1");
			params.put("currency", currency);
			// 请求测试
			String json = httpService.getJsonPost(params);
			System.out.println("testOrder 结果: " + json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	public HttpService getHttpService() {
		return httpService;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
	
}
