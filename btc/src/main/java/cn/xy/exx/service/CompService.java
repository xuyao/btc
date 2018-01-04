package cn.xy.exx.service;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.xy.exx.util.ConstsUtil;
import cn.xy.exx.vo.AccountInfo;
import cn.xy.exx.vo.AskBid;
import cn.xy.exx.vo.Deal;
import cn.xy.exx.service.LogService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class CompService extends LogService{

	@Autowired
	HttpService httpService;
	
	Double usd_cny = ConstsUtil.getCnyUsd();//汇率
	Double comp_cny_usd = ConstsUtil.getCompCnyUsd();//人民币比美元
	Double comp_usd_cny = ConstsUtil.getCompUsdCny();//美元比人民币
	
	
	//获得用户信息
	public AccountInfo getAccountInfo(){
		String ha = "https://trade.exx.com/api/getBalance";
		AccountInfo ai = null;
		try {
			// 需加密的请求参数
			Map<String, String> params = new TreeMap<String, String>();
			String json = httpService.get(ha, params);
			System.out.println(json);
			JSONObject result = JSON.parseObject(json);
			if(result == null)
				return null;
			result = result.getJSONObject("funds");
			
			ai = new AccountInfo();
			ai.setQcAvailable(result.getJSONObject("QC").getDouble("balance"));
			ai.setUsdtAvailable(result.getJSONObject("USDT").getDouble("balance"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ai;
	}
	
	
	//得到挂单的买卖价格和数量
	public AskBid getAskBid(String market){
		String ha = "https://api.exx.com/data/v1/depth?currency="+market;
		String result = httpService.get(ha, null);
		if(StringUtils.isEmpty(result))//如果行情没取到直接返回
			return null;

		JSONArray asksArr = JSON.parseObject(result).getJSONArray("asks");
		JSONArray bidsArr = JSON.parseObject(result).getJSONArray("bids");
		if(asksArr==null || bidsArr==null)
			return null;
		JSONArray asks1 = asksArr.getJSONArray(asksArr.size()-1);
		JSONArray bids1 = bidsArr.getJSONArray(0);
		
		AskBid ab = new AskBid();
		ab.setAsk1(asks1.getDouble(0));
		ab.setAsk1_amount(asks1.getDouble(1));
		ab.setBid1(bids1.getDouble(0));
		ab.setBid1_amount(bids1.getDouble(1));
		ab.setMarket(market);
//		System.out.println(market+" "+asks1.getDouble(0)+asks1.getDouble(1)+" "
//				+bids1.getDouble(0)+" "+bids1.getDouble(1));
		return ab;
	}
	
	
	//嗅探
	public Double sniffCnyUsd(AskBid ab1, AskBid ab2){
		Double d = (ab2.getBid1()*usd_cny)/ab1.getAsk1();
		return d;
	}
	
	//嗅探
	public Double sniffUsdCny(AskBid ab1, AskBid ab2){
		Double d = ab2.getBid1()/(ab1.getAsk1()*usd_cny);
		return d;
	}
	
	//比较两个市场的套利价格,从ab1买，去ab2卖，第一个参数是人民币，第二个参数是usd
	public Deal compCnyUsd(AskBid ab1, AskBid ab2){
		Deal deal =null;
		if((ab2.getBid1()*usd_cny)/ab1.getAsk1()>comp_cny_usd){//如果价格之差大于ab2的1.2%认为有利可图,后改成千分之6
			deal = new Deal();
			deal.setBuyPrice(ab1.getAsk1());//买入价格设置为ab1的卖一价格
			deal.setSellPrice(ab2.getBid1());//卖出价格设置为ab2的买一价格
			deal.setBuyMarket(ab1.getMarket());
			
			deal.setBuyAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//量取小的那个
			deal.setSellAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//同上，买卖量相同
			deal.setSellMarket(ab2.getMarket());
		}
		return deal;
	}
	

	//第一个参数是usd，第二个参数是人民币
	public Deal compUsdCny(AskBid ab1, AskBid ab2){
		Deal deal =null;
		if(ab2.getBid1()/(ab1.getAsk1()*usd_cny)>comp_usd_cny){//如果价格之差大于ab2的1.2%认为有利可图，后改成2.7%
			deal = new Deal();
			deal.setBuyPrice(ab1.getAsk1());//买入价格设置为ab1的卖一价格
			deal.setSellPrice(ab2.getBid1());//卖出价格设置为ab2的买一价格
			deal.setBuyMarket(ab1.getMarket());
			
			deal.setBuyAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//量取小的那个
			deal.setSellAmount(Math.min(ab1.getAsk1_amount(), ab2.getBid1_amount()));//同上，买卖量相同
			deal.setSellMarket(ab2.getMarket());
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
