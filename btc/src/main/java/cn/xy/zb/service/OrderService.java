package cn.xy.zb.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.xy.zb.Market;
import cn.xy.zb.Tax;
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
	Double qc_limit = 0d;//qc限制
	Double usdt_limit = 0d;//初始化美元
	Double profit = ConstsUtil.getProfit();//得到利益的下限
	
	//qc_usdt
	public void dealQc2Usdt(Deal deal, AccountInfo ai){
		if(deal==null)
			return;
		Double buyPrice = deal.getBuyPrice();
		Double sellPrice = deal.getSellPrice();
		sellPrice = NumberUtil.doubleMul(sellPrice, usd_cny);
		
		//计算amount
		qc_limit = ai.getQcAvailable();
		Double amount = qc_limit/buyPrice;//qc的价格折算的数量
		if(amount>deal.getBuyAmount())//如果限制仓位下的amount小于挂单买入的amount，以小的为准
			amount=deal.getBuyAmount();
		amount =  getAmount(deal.getBuyMarket(), amount);//买入量按照市场进行小数点转换
		if(amount==0)//如果买不起了，就不要操作了
			return;
		
		doOrder(deal, amount);
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
		System.out.println("qc*****************************************");
	}
	
	
	//usdt_qc
	public void dealUsdt2Qc(Deal deal, AccountInfo ai){
		if(deal==null)
			return;
		
		Double buyPrice = deal.getBuyPrice();
		Double sellPrice = deal.getSellPrice();
		buyPrice = NumberUtil.doubleMul(buyPrice, usd_cny);

		usdt_limit = ai.getUsdtAvailable();
		Double amount = usdt_limit/buyPrice;//qc的价格折算的数量
		if(amount>deal.getBuyAmount())//如果限制仓位下的amount小于挂单买入的amount，以小的为准
			amount=deal.getBuyAmount();
		amount =  getAmount(deal.getBuyMarket(), amount);//买入量按照市场进行小数点转换
		
		if(amount==0)//如果买不起了，就不要操作了
			return;
		
		doOrder(deal, amount);
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
		System.out.println("usdt*****************************************");
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
						//卖出时候的b，数量有变化,根据买入市场的数量，买b引起数量变化，卖出影响金额变化
						Double tax = Tax.map.get(deal.getBuyMarket());
						amount = getAmount(deal.getBuyMarket(), amount*(1-tax));
						Result sellResult = order(deal.getSellMarket(), "0", String.valueOf(deal.getSellPrice()),String.valueOf(amount));//卖出
						System.out.println("sellResult code:"+sellResult.getCode());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++;
				}while(i<5);//5次
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
			params.put("tradeType", tradeType);
			params.put("currency", currency);
			// 请求测试
			String json = httpService.getJsonPost(params);
			JSONObject jsonObj = JSONObject.parseObject(json);
			result = jsonObj.parseObject(json, Result.class);
			System.out.println(price+" "+" "+amount+"交易结果: " + json);
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
