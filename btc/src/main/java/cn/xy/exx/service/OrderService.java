package cn.xy.exx.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.xy.exx.Market;
import cn.xy.exx.util.ConstsUtil;
import cn.xy.exx.util.DateUtil;
import cn.xy.exx.util.NumberUtil;
import cn.xy.exx.vo.AccountInfo;
import cn.xy.exx.vo.Deal;
import cn.xy.exx.vo.Result;
import cn.xy.exx.service.LogService;
import cn.xy.exx.vo.Order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class OrderService extends LogService{
	
	@Autowired
	HttpService httpService;
	
	Double usd_cny = ConstsUtil.getCnyUsd();//汇率
	Double qc_limit = 0d;//qc限制
	Double usdt_limit = 0d;//初始化美元
	Double profit = ConstsUtil.getProfit();//得到利益的下限
	
	//qc_usdt
	public void dealQc2Usdt(Deal deal, AccountInfo ai){
		if(deal==null || ai == null)
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
		if(amount==0 || amount*buyPrice<profit*usd_cny)//如果买不起了，或者金额太小，就不要操作了
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
		logger.info("exx:"+sb.toString());  
		logger.info("exx_qc*****************************************");
	}
	
	
	//usdt_qc
	public void dealUsdt2Qc(Deal deal, AccountInfo ai){
		if(deal==null || ai==null)
			return;
		
		Double buyPrice = deal.getBuyPrice();
		Double sellPrice = deal.getSellPrice();

		usdt_limit = ai.getUsdtAvailable();
		Double amount = usdt_limit/buyPrice;//qc的价格折算的数量
		if(amount>deal.getBuyAmount())//如果限制仓位下的amount小于挂单买入的amount，以小的为准
			amount=deal.getBuyAmount();
		amount =  getAmount(deal.getBuyMarket(), amount);//买入量按照市场进行小数点转换
		
		if(amount==0 || amount*buyPrice<profit)//如果买不起了，或者金额太小，就不要操作了
			return;
		
		buyPrice = NumberUtil.doubleMul(buyPrice, usd_cny);
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
		logger.info("exx:"+sb.toString());  
		logger.info("exx_usdt*****************************************");  
	}
	
	
	//得到相应市场的买入数量
	public Double getAmount(String market, Double amount){
		return NumberUtil.formatDouble(amount, Market.map.get(market).getAmountScale());
	}
	
	
	//循环等待请求
	public void doOrder(Deal deal, Double amount){
		Result buyResult = order(deal.getBuyMarket(), "buy", String.valueOf(deal.getBuyPrice()),String.valueOf(amount));//买入
		if(buyResult!=null){//请求接口成功
			if("100".equals(buyResult.getCode())){
				int i=0;
				do{
					//卖出时候的b，数量有变化,根据买入市场的数量，买b引起数量变化，卖出影响金额变化
//					Double tax = Tax.map.get(deal.getBuyMarket());
//					目前exx全部交易都是0.1%
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Double tax = 0.001;
					amount = getAmount(deal.getBuyMarket(), amount*(1-tax));
					Result sellResult = order(deal.getSellMarket(), "sell", String.valueOf(deal.getSellPrice()),String.valueOf(amount));//卖出
					logger.info("exx sellResult code:"+sellResult.getCode());
					i++;

				}while(i<2);//2次
			}
		}
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	
	//下单 tradeType交易类型[buy/sell]
	public Result order(String currency, String tradeType,String price, String amount){
		Result result = null;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("price", price);
			params.put("amount", amount);
			params.put("type", tradeType);
			params.put("currency", currency);
			// 请求测试
			String json = httpService.get("https://trade.exx.com/api/order", params);
			JSONObject jsonObj = JSONObject.parseObject(json);
			result = jsonObj.parseObject(json, Result.class);
			logger.info("exx "+price+" "+amount+"交易结果: " + json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	

	public List<Order> getUnfinishedOrdersIgnoreTradeType(String market) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("currency", market);
		params.put("pageIndex", "1");
		params.put("type", "buy");
		String json = httpService.get("https://trade.exx.com/api/getOpenOrders", params);
		System.out.println(json);
		if(StringUtils.isEmpty(json) || json.startsWith("{")){
			return null;
		}
		JSONArray jsonArry = JSONArray.parseArray(json);
		Iterator it = jsonArry.iterator();
		List<Order> list = new ArrayList<Order>();
		while(it.hasNext()){
			JSONObject jsonObj = (JSONObject)it.next();
			if(!"0".equals(jsonObj.getString("type")) && !"3".equals(jsonObj.getString("type")))//如果不是未成交，跳出
				continue;
			Order order = new Order();
			order.setCurrency(jsonObj.getString("currency"));
			order.setId(jsonObj.getString("id"));
			order.setPrice(jsonObj.getDouble("price"));
			order.setStatus(jsonObj.getInteger("status"));
			order.setTotal_amount(jsonObj.getDouble("total_amount"));
			order.setTrade_amount(jsonObj.getDouble("trade_amount"));
			order.setTrade_date(jsonObj.getInteger("trade_date"));
			order.setTrade_money(jsonObj.getDouble("trade_money"));
			order.setType(jsonObj.getString("type"));
			list.add(order);
		}
		return list;	
	}
	
	//cancel order
	public void cancelOrder(Order order) {
		String orderId = order.getId();//
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", orderId);
			params.put("currency", order.getCurrency());
			String json = httpService.get("https://trade.exx.com/api/cancel", params);
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
