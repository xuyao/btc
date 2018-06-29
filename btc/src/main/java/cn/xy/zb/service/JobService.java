package cn.xy.zb.service;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import com.plato.common.cache.memcached.MemcachedCache;

import cn.xy.zb.Market;
import cn.xy.zb.util.ConstsUtil;
import cn.xy.zb.vo.AccountInfo;
import cn.xy.zb.vo.AskBid;
import cn.xy.zb.vo.Deal;

@DisallowConcurrentExecution
@Service
public class JobService extends QuartzJobBean {

	public static Logger logger = Logger.getLogger(JobService.class);
	
	@Autowired
	CompService compService;
	@Autowired
	OrderService orderService;
	@Autowired
	ConstService constService;
	
	AccountInfo ai = null;
	String sniff = ConstsUtil.getSniff();
	Double sniffCnyUsd = 0d;
	Double sniffUsdCny = 0d;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		String markets = (String) context.getJobDetail().getJobDataMap().get("markets");
		String mark = (String) context.getJobDetail().getJobDataMap().get("mark");
		
		MemcachedCache memcachedClient = compService.memcachedClient;
		Boolean isFirstRun = constService.map.get(mark);
		if(isFirstRun==null || isFirstRun==true) {//第一次初始化
			Market.init(markets);
			compService.usd_cny = compService.getTicker("usdt_qc").getLast();
			orderService.usd_cny = compService.getTicker("usdt_qc").getLast();
			constService.map.put(mark, false);
		}
		
		ai = compService.getAccountInfo();
		//循环市场
		String[][] arry = Market.arry;
		for(String[] sa : arry){
			detail(sa[0], sa[1]);
		}
		Double hl = (Double)memcachedClient.get("hl");
		orderService.usd_cny = hl;
		compService.usd_cny = hl;
		logger.info(mark);
		
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
			Deal deal_ac_usdt = compService.compCnyUsd(ab_qc, ab_usdt);//cny杞瑄sd
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
