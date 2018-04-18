package cn.xy.okex.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.okcoin.rest.stock.IStockRestApi;
import com.okcoin.rest.stock.impl.StockRestApi;

import cn.xy.okex.Market;
import cn.xy.okex.vo.AskBid;

@Service
public class CancelService extends LogService{

	@Autowired
	CompService compService;
	
    String api_key = "";  //OKCoin申请的apiKey
   	String secret_key = "";  //OKCoin 申请的secret_key
	String url_prex = "https://www.okcoin.cn";  //注意：请求URL 国际站https://www.okcoin.com ; 国内站https://www.okcoin.cn
	
	
	IStockRestApi stockPost = new StockRestApi(url_prex, api_key, secret_key);
	
	
	public void work(){
		
		//查询账户, 先处理下余额
		doRemain();
		
		//撤单
	    //现货下单交易
	    //现货获取用户订单信息
        try {
			String ordersjson = stockPost.order_info("btc_usd", "-1");
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        

		logger.info("cancel*");
	}

	
	private void doRemain(){
		try {
			String userinfojson = stockPost.userinfo();
			JSONObject json = JSONObject.parseObject(userinfojson);
			JSONObject free = json.getJSONObject("info").getJSONObject("funds").getJSONObject("free");
			List<String> list = Market.list;
			for(String s : list) {
				double freeamount = free.getDouble(s);
				if(freeamount>0.01) {//如果大于最小单位，则下单卖出
					AskBid ab_exnbtc = compService.getAskBid(s+"_btc");// xxx/btc
					String tradeResult = stockPost.trade(s+"_btc", "sell", "50", 
							String.valueOf(ab_exnbtc.getAsk1()-0.001));//卖一减去最小单位
				}
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
}
