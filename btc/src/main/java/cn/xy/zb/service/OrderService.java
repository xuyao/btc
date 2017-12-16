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
import cn.xy.zb.vo.Result;

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
		sellPrice = NumberUtil.doubleMul(sellPrice, usd_cny);

		Double amount = qc_limit/buyPrice;//qc的价格折算的数量
		if(amount>deal.getBuyAmount())//如果限制仓位下的amount小于挂单买入的amount，以小的为准
			amount=deal.getBuyAmount();
		amount =  getAmount(deal.getBuyMarket(), amount);//买入量按照市场进行小数点转换
		
//		doOrder(deal, amount);
		System.out.println("qc*****************************************"+amount);
		StringBuilder sb = new StringBuilder(DateUtil.formatLongPattern(new Date())).append("\n");
		sb.append(" 市场：").append(deal.getBuyMarket());
		sb.append(" ").append(deal.getBuyPrice()).append(" ").append(buyPrice);
		sb.append(" ").append(deal.getBuyAmount());
		sb.append("\n");
		sb.append(" 市场：").append(deal.getSellMarket());
		sb.append(" ").append(deal.getSellPrice()).append(" ").append(sellPrice);
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
		buyPrice = NumberUtil.doubleMul(buyPrice, usd_cny);

		Double amount = qc_limit/buyPrice;//qc的价格折算的数量
		if(amount>deal.getBuyAmount())//如果限制仓位下的amount小于挂单买入的amount，以小的为准
			amount=deal.getBuyAmount();
		amount =  getAmount(deal.getBuyMarket(), amount);//买入量按照市场进行小数点转换
		
//		doOrder(deal, amount);
		System.out.println("usdt*****************************************"+amount);
		
		StringBuilder sb = new StringBuilder(DateUtil.formatLongPattern(new Date())).append("\n");
		sb.append(" 市场：").append(deal.getBuyMarket());
		sb.append(" ").append(deal.getBuyPrice()).append(" ").append(buyPrice);
		sb.append(" ").append(deal.getBuyAmount());
		sb.append("\n");
		sb.append(" 市场：").append(deal.getSellMarket());
		sb.append(" ").append(deal.getSellPrice()).append(" ").append(sellPrice);
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
	
	
	//循环等待请求
	public void doOrder(Deal deal, Double amount){
		Result buyResult = order(deal.getBuyMarket(), "1", String.valueOf(deal.getBuyPrice()),String.valueOf(amount));//买入
		if(buyResult!=null){//请求接口成功
			if("1000".equals(buyResult.getCode())){
				int i=0;
				do{
					try {
						Thread.sleep(320);//现成休眠320毫秒，等待买入成功
						Result sellResult = order(deal.getSellMarket(), "0", String.valueOf(deal.getSellPrice()),String.valueOf(amount));//卖出
						System.out.println("sellResult code:"+sellResult.getCode());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++;
				}while("1000".equals(i>7));//6次
			}
		}
	}
	
	
	//下单 tradeType交易类型1/0[buy/sell]
	public Result order(String currency, String tradeType,String price, String amount){
		Result result = null;
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
			JSONObject jsonObj = JSONObject.parseObject(json);
			result = jsonObj.parseObject(json, Result.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	

	public HttpService getHttpService() {
		return httpService;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
	
}
