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
	
	
	//鑾峰緱鐢ㄦ埛淇℃伅
	public AccountInfo getAccountInfo(){
		String ha = "https://parseObject/api/getBalance";
		AccountInfo ai = null;
		try {
			// 闇�鍔犲瘑鐨勮姹傚弬鏁�
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
	
	
	//寰楀埌鎸傚崟鐨勪拱鍗栦环鏍煎拰鏁伴噺
	public AskBid getAskBid(String market){
		String ha = "https://www.okex.cn/api/v1/depth.do?symbol="+market+"&size=2";;
		String result = httpService.get(ha, null);
		if(StringUtils.isEmpty(result))//濡傛灉琛屾儏娌″彇鍒扮洿鎺ヨ繑鍥�
			return null;

		JSONArray asksArr = JSON.parseObject(result).getJSONArray("asks");
		JSONArray bidsArr = JSON.parseObject(result).getJSONArray("bids");
		if(asksArr==null || bidsArr==null)
			return null;
		JSONArray asks1 = asksArr.getJSONArray(asksArr.size()-1);//鍗栦竴
		JSONArray bids1 = bidsArr.getJSONArray(0);//涔颁竴
		
		JSONArray asks2 = asksArr.getJSONArray(asksArr.size()-2);//鍗栦簩
		JSONArray bids2 = bidsArr.getJSONArray(1);//涔颁簩
		
		AskBid ab = new AskBid();
		ab.setAsk1(asks1.getDouble(0));//鍗栦竴
		ab.setAsk1_amount(asks1.getDouble(1));
		ab.setBid1(bids1.getDouble(0));//涔颁竴
		ab.setBid1_amount(bids1.getDouble(1));
		ab.setMarket(market);
		
		ab.setAsk2(asks2.getDouble(0));//鍗栦簩
		ab.setAsk2_amount(asks2.getDouble(1));
		ab.setBid2(bids2.getDouble(0));//涔颁簩
		ab.setBid2_amount(bids2.getDouble(1));

		return ab;
	}
	
	
}
