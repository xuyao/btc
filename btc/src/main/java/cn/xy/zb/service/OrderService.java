package cn.xy.zb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.plato.common.cache.memcached.MemcachedCache;

import cn.xy.zb.Market;
import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.util.DateUtil;
import cn.xy.zb.util.NumberUtil;
import cn.xy.zb.vo.AccountInfo;
import cn.xy.zb.vo.Deal;
import cn.xy.zb.vo.MarketAB;
import cn.xy.zb.vo.Order;
import cn.xy.zb.vo.Result;

@Service
public class OrderService extends LogService{
	
	@Autowired
	HttpService httpService;
	MemcachedCache memcachedClient = MemcacheFactory.getClient();
	
//	public Double usd_cny = ConstsUtil.getCnyUsd();//汇率
	public Double usd_cny = (Double)memcachedClient.get("hl");//汇率
	Double profit = ConstsUtil.getProfit();//得到利益的下限
	Double tax = ConstsUtil.getTax();
	
	//qc_usdt
	public void dealQc2Usdt(Deal deal, AccountInfo ai){
		if(deal==null || ai == null)
			return;
		Double buyPrice = deal.getBuyPrice();
		Double sellPrice = deal.getSellPrice();
		sellPrice = NumberUtil.doubleMul(sellPrice, usd_cny);
		Double qc_limit = ConstsUtil.getQcLimit();//qc限制
		//计算amount
		if(qc_limit>ai.getQcAvailable())
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
		logger.info(sb.toString());
		logger.info("qc*****************************************");
	}
	
	
	//usdt_qc
	public void dealUsdt2Qc(Deal deal, AccountInfo ai){
		if(deal==null || ai==null)
			return;
		
		Double buyPrice = deal.getBuyPrice();
		Double sellPrice = deal.getSellPrice();
		Double usdt_limit = ConstsUtil.getUsdtLimit();//初始化美元
		
		if(usdt_limit>ai.getUsdtAvailable())
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
		logger.info(sb.toString());  
		logger.info("usdt*****************************************");  
	}
	
	
	//得到相应市场的买入数量
	public Double getAmount(String market, Double amount){
		MarketAB mab = Market.map.get(market);
		if(mab==null)
			return null;
		return NumberUtil.formatDouble(amount, mab.getAmountScale());
	}
	//得到相应市场的最小价格
	public Double getMinPrice(String market){
		MarketAB mab = Market.map.get(market);
		if(mab==null)
			return null;
		return Math.pow(10, -mab.getPriceScale());
	}
	
	
	//循环等待请求
	public void doOrder(Deal deal, Double amount){
		Result buyResult = order(deal.getBuyMarket(), "1", String.valueOf(deal.getBuyPrice()),String.valueOf(amount));//买入
		if(buyResult!=null){//请求接口成功
			if("1000".equals(buyResult.getCode())){
				int i=0;
				do{
					//卖出时候的b，数量有变化,根据买入市场的数量，买b引起数量变化，卖出影响金额变化
//					Double tax = Tax.map.get(deal.getBuyMarket());
					amount = getAmount(deal.getBuyMarket(), amount*(1-tax));
					Result sellResult = order(deal.getSellMarket(), "0", 
							String.valueOf(deal.getSellPrice() - getMinPrice(deal.getSellMarket())),String.valueOf(amount));//卖出
					logger.info("sellResult code:"+sellResult.getCode());
					i++;
				}while(i<3);//1次
			}
		}
		java.awt.Toolkit.getDefaultToolkit().beep();
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
			logger.info(currency + price+" "+amount+"交易结果: " + json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	
	public List<Order> getUnfinishedOrdersIgnoreTradeType(String market) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("method", "getUnfinishedOrdersIgnoreTradeType");
		params.put("currency", market);
		params.put("pageIndex", "1");
		params.put("pageSize", "10");
		String json = httpService.getJsonPost(params);
		System.out.println(market+json);
		if(StringUtils.isEmpty(json) || json.startsWith("{")){
			return null;
		}
		JSONArray jsonArry = JSONArray.parseArray(json);
		Iterator it = jsonArry.iterator();
		List<Order> list = new ArrayList<Order>();
		while(it.hasNext()){
			JSONObject jsonObj = (JSONObject)it.next();
			Order order = new Order();
			order.setCurrency(jsonObj.getString("currency"));
			order.setId(jsonObj.getString("id"));
			order.setPrice(jsonObj.getDouble("price"));
			order.setStatus(jsonObj.getInteger("status"));
			order.setTotal_amount(jsonObj.getDouble("total_amount"));
			order.setTrade_amount(jsonObj.getDouble("trade_amount"));
			order.setTrade_date(jsonObj.getLong("trade_date"));
			order.setTrade_money(jsonObj.getDouble("trade_money"));
			order.setType(jsonObj.getInteger("type"));
			list.add(order);
		}
		return list;	
	}
	
	//cancel order
	public void cancelOrder(Order order) {
		String orderId = order.getId();//
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("method", "cancelOrder");
			params.put("id", orderId);
			params.put("currency", order.getCurrency());

			String json = httpService.getJsonPost(params);
			logger.info(order.getCurrency()+"cancelOrder 结果: " + json);
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
