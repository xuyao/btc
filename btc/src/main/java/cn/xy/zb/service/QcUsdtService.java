package cn.xy.zb.service;

import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.util.NumberUtil;
import cn.xy.zb.vo.AccountInfo;
import cn.xy.zb.vo.AskBid;
import cn.xy.zb.vo.Order;

import com.plato.common.cache.memcached.MemcachedCache;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class QcUsdtService extends LogService{
	
  @Autowired
  HttpService httpService;
  @Autowired
  CompService compService;
  @Autowired
  OrderService orderService;
  
  MemcachedCache memcachedClient = MemcacheFactory.getClient();
  
  Integer second = ConstsUtil.getSecond();
  String smma = ConstsUtil.getValue("mma");
  String bsusdt = ConstsUtil.getValue("bsusdt");
//  String urlbuy = "https://api-otc.huobi.pro/v1/otc/trade/list/public?coinId=2&tradeType=1&currentPage=1&payWay=&country=&merchant=1&online=1&range=0";
//  String urlsell = "https://api-otc.huobi.pro/v1/otc/trade/list/public?coinId=2&tradeType=0&currentPage=1&payWay=&country=&merchant=1&online=1&range=0";
  
  public void work(){
/*************火币*********/
//    String json = this.httpService.get(this.urlbuy);
//    JSONObject jsonObj = JSONObject.parseObject(json);
//    String code = jsonObj.getString("code");
//    if (!"200".equals(code)) {
//      return;
//    }
//    double buyPrice = 0;
//    
//    JSONArray dataArray = jsonObj.getJSONArray("data");
//    Iterator it = dataArray.iterator();
//    int i = 0;
//    while (it.hasNext()){
//      JSONObject data = (JSONObject)it.next();
//      buyPrice = data.getDouble("price").doubleValue()+buyPrice;
//      i++;
//    }
/*************ma*********/
//	int size =55;
//	String json = httpService.get("http://api.zb.com/data/v1/kline?market=usdt_qc&type=1hour&size="+size);
//	JSONObject jsonObj = JSONObject.parseObject(json);
//	JSONArray jsArr = jsonObj.getJSONArray("data");
//	Iterator it = jsArr.listIterator();
//	double ma = 0;
//	while(it.hasNext()){
//		JSONArray jsa = (JSONArray)it.next();
//		ma = ma+jsa.getDoubleValue(4);
//	}
//		
//    ma =NumberUtil.formatDoubleHP(ma/size, 4);
//    memcachedClient.set("ma", ma);
//    System.out.println(buyPrice);
 
/*************gogo*********/ 
//    double buyPrice = 0;
//    double top = buyPrice + 0.0350;
//    double bottom = buyPrice - 0.0350;
    
    AskBid ab_qc = compService.getAskBid("usdt_qc");
    
    AccountInfo ai = compService.getAccountInfo();
    Double qc = ai.getQcAvailable();
    Double usdt = ai.getUsdtAvailable();
    Double ma  = (Double)memcachedClient.get("ma");//取得均线
    List<Order> orderList = this.orderService.getUnfinishedOrdersIgnoreTradeType("usdt_qc");
    boolean ifsell = true;
    boolean ifbuy = true;
    
    if(orderList!=null) {
        for(Order o : orderList) {
        	if(o.getType()==0 && o.getPrice().compareTo(ab_qc.getAsk2())==0) {//如果是卖单，且是卖一价格
        		ifsell = false;
        	}
        	if(o.getType()==1 && o.getPrice().compareTo(ab_qc.getBid1())==0) {//如果是买单，且是买一价格
        		ifbuy = false;
        	}
        	
        	long sys = System.currentTimeMillis();
        	if (sys - o.getTrade_date().longValue() > this.second.intValue() * 2 * 1000) {//如果当前价格等于卖一或者买一
    			if(o.getPrice().compareTo(ab_qc.getAsk2())!=0 && o.getPrice().compareTo(ab_qc.getBid1())!=0)
    				orderService.cancelOrder(o);
        	}
        }
    }

    if(ifbuy && qc>3000) {
	    if (ab_qc.getBid1().doubleValue() < ma-0.03){//买单
	    	double amount = NumberUtil.geScaretDouble(1,2);
	    	if (ab_qc.getBid1().doubleValue() < ma-0.04){
	    		amount = NumberUtil.geScaretDouble(5,10);
	    	}
	    	if (ab_qc.getBid1().doubleValue() < ma-0.05){
	    		amount = NumberUtil.geScaretDouble(25,50);
	    	}
	    	if (ab_qc.getBid1().doubleValue() < ma-0.06){
	    		amount = NumberUtil.geScaretDouble(125,250);
	    	}
	    	if (ab_qc.getBid1().doubleValue() < ma-0.07){
	    		amount = NumberUtil.geScaretDouble(300,450);
	    	}
	    	amount = Math.min(amount, new Double(NumberUtil.doubleDiv(qc, ab_qc.getBid1().doubleValue(), 4)).intValue());
	    	orderService.order("usdt_qc", "1", String.valueOf(ab_qc.getBid1().doubleValue() + 0.0001), String.valueOf(amount));
	    }
    }
    
    if("t".equals(bsusdt) && qc<1800) {//如果qc小于2000
    	orderService.order("usdt_qc", "0", String.valueOf(ab_qc.getAsk2().doubleValue() - 0.0001), "50");//卖出100
    }
    
    if(ifsell && usdt>460) {
    	if(smma!=null && !"".equals(smma)) {//如果mma有值，就是说明要手动控制价格了
    		ma = Double.parseDouble(smma);
    	}
    	if (ab_qc.getAsk2().doubleValue() > ma+0.03){//卖单
    		double amount = NumberUtil.geScaretDouble(1,2);
	      	if (ab_qc.getAsk2().doubleValue() > ma+0.04){
	      		amount = NumberUtil.geScaretDouble(5,10);
	      	}
	      	if (ab_qc.getAsk2().doubleValue() > ma+0.05){
	      		amount = NumberUtil.geScaretDouble(25,50);
	      	}
	      	if (ab_qc.getAsk2().doubleValue() > ma+0.06){
	      		amount = NumberUtil.geScaretDouble(125,250);
	      	}
	      	if (ab_qc.getAsk2().doubleValue() > ma+0.07){
	      		amount = NumberUtil.geScaretDouble(300,400);
	      	}
	      	amount = Math.min(amount, usdt.intValue());
	      	orderService.order("usdt_qc", "0", String.valueOf(ab_qc.getAsk2().doubleValue() - 0.0001), String.valueOf(amount));
    	}
    }
    
    if("t".equals(bsusdt) && usdt<280) {
    	orderService.order("usdt_qc", "1", String.valueOf(ab_qc.getBid1().doubleValue() + 0.0001), "50");//买入100
    }
    
    
//    if(ifbuy) {
//	    if (ab_qc.getBid1().doubleValue() < 6.5610){//买单
//	    	int amount = NumberUtil.geScaretInt(2, 2);
//	    	if (ab_qc.getBid1().doubleValue() < 6.5510){
//	    		amount = NumberUtil.geScaretInt(40, 40);
//	    	}
//	    	if (ab_qc.getBid1().doubleValue() < 6.5420){
//	    		amount = NumberUtil.geScaretInt(100, 100);
//	    	}
//	    	if (ab_qc.getBid1().doubleValue() < 6.5320){
//	    		amount = NumberUtil.geScaretInt(200, 200);
//	    	}
//	    	amount = Math.min(amount, qc.intValue());
//	    	orderService.order("usdt_qc", "1", String.valueOf(ab_qc.getBid1().doubleValue() + 0.0001), String.valueOf(amount));
//	    }
//    }
//    
//    if(ifsell) {
//    	if (ab_qc.getAsk2().doubleValue() > 6.6190){//卖单
//	      	int amount = NumberUtil.geScaretInt(2, 2);
//	      	if (ab_qc.getAsk2().doubleValue() > 6.6290){
//	      		amount = NumberUtil.geScaretInt(40, 40);
//	      	}
//	      	if (ab_qc.getAsk2().doubleValue() > 6.6390){
//	      		amount = NumberUtil.geScaretInt(100, 100);
//	      	}
//	      	if (ab_qc.getAsk2().doubleValue() > 6.6490){
//	      		amount = NumberUtil.geScaretInt(200, 200);
//	      	}
//	      	amount = Math.min(amount, usdt.intValue());
//	      	orderService.order("usdt_qc", "0", String.valueOf(ab_qc.getAsk2().doubleValue() - 0.0001), String.valueOf(amount));
//    	}
//    }
    
    logger.info("usdt_qc.");
  }
  
}
