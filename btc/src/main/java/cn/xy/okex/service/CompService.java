package cn.xy.okex.service;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.xy.okex.vo.AccountInfo;
import cn.xy.okex.vo.AskBid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class CompService extends LogService{

	@Autowired
	HttpService httpService;
	
	
	//获得用户信息
	public AccountInfo getAccountInfo(){
		String ha = "https://parseObject/api/getBalance";
		AccountInfo ai = null;
		try {
			// 需加密的请求参数
			Map<String, String> params = new TreeMap<String, String>();
			String json = httpService.get(ha, params);
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
		String ha = "https://www.okex.cn/api/v1/depth.do?symbol="+market;
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
	
	
}
