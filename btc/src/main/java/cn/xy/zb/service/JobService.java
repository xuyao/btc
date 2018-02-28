package cn.xy.zb.service;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plato.common.cache.memcached.MemcachedCache;

import cn.xy.zb.Market;
import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.util.NumberUtil;
import cn.xy.zb.vo.AccountInfo;
import cn.xy.zb.vo.AskBid;
import cn.xy.zb.vo.Deal;
import cn.xy.zb.vo.Ticker;

@Service
public class JobService extends LogService{

	@Autowired
	CompService compService;
	@Autowired
	OrderService orderService;
	MemcachedCache memcachedClient = MemcacheFactory.getClient();
	
	AccountInfo ai = null;
	String sniff = ConstsUtil.getSniff();
	Double sniffCnyUsd = 0d;
	Double sniffUsdCny = 0d;
	Integer qsize = ConstsUtil.getQueueSize();
//	Queue<Double> queue = new ArrayDeque<Double>();
	
	public void work(){
		
		String on = (String)memcachedClient.get("on");
		if(!"t".equals(on))
			return ;
		//查询账户
		ai = compService.getAccountInfo();
		//循环市场
		String[][] arry = Market.arry;
		for(String[] sa : arry){
			detail(sa[0], sa[1]);
		}
		
		Double hl = (Double)memcachedClient.get("hl");
		orderService.usd_cny = hl;
		compService.usd_cny = hl;
		logger.info(".");
	}
	
	
	private double cmpQueue(Queue<Double> queue) {
		Iterator it = queue.iterator();
		Map<Double,Integer> m = new HashMap<Double,Integer>();
		Double result = 6.61;
		Double sum = 0d;
		while(it.hasNext()) {//滑动平均线
			Double d = (Double)it.next();
			sum = sum+d;
		}
		return NumberUtil.formatDoubleHP((sum/queue.size()), 3);
	}
	
	
	public void detail(String abqc, String abusdt){
		AskBid ab_qc = compService.getAskBid(abqc);//qc叫价
		AskBid ab_usdt = compService.getAskBid(abusdt);//usdt叫价
		if(ab_qc==null || ab_usdt==null)
			return ;//如果为空，就返回
		
		if("t".equals(sniff)){//
			sniffCnyUsd = Math.max(compService.sniffCnyUsd(ab_qc, ab_usdt), sniffCnyUsd);
			sniffUsdCny = Math.max(compService.sniffUsdCny(ab_usdt, ab_qc), sniffUsdCny);
		}else{
//			sniffCnyUsd = Math.max(compService.sniffCnyUsd(ab_qc, ab_usdt), sniffCnyUsd);
//			sniffUsdCny = Math.max(compService.sniffUsdCny(ab_usdt, ab_qc), sniffUsdCny);
			Deal deal_ac_usdt = compService.compCnyUsd(ab_qc, ab_usdt);//cny转usd
			orderService.dealQc2Usdt(deal_ac_usdt, ai);
			
			Deal deal_usdt_qc = compService.compUsdCny(ab_usdt, ab_qc);
			orderService.dealUsdt2Qc(deal_usdt_qc, ai);
		}

	}

	
	public OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public CompService getCompService() {
		return compService;
	}

	public void setCompService(CompService compService) {
		this.compService = compService;
	}
	
}
