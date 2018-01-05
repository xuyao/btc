package cn.xy.exx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xy.exx.Market;
import cn.xy.exx.util.ConstsUtil;
import cn.xy.exx.vo.AccountInfo;
import cn.xy.exx.vo.AskBid;
import cn.xy.exx.vo.Deal;
import cn.xy.exx.service.LogService;

@Service
public class JobService extends LogService{

	@Autowired
	CompService compService;
	@Autowired
	OrderService orderService;
	
	AccountInfo ai = null;
	String sniff = ConstsUtil.getSniff();
	Double sniffCnyUsd = 0d;
	Double sniffUsdCny = 0d;
	String direction = ConstsUtil.getDirection();
	
	public void work(){
		//查询账户
		ai = compService.getAccountInfo();
		//循环市场
		String[][] arry = Market.arry;
		for(String[] sa : arry){
			detail(sa[0], sa[1]);
		}
		
		if("t".equals(sniff)){
			System.out.println("cny to usdt:"+sniffCnyUsd);
			System.out.println("usdt to cny:"+sniffUsdCny);
			System.out.println();
			sniffCnyUsd = 0d;
			sniffUsdCny = 0d;
		}
		logger.info("exx.");
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
			if("1".equals(direction)){
				Deal deal_ac_usdt = compService.compCnyUsd(ab_qc, ab_usdt);//cny转usd
				orderService.dealQc2Usdt(deal_ac_usdt, ai);
			}
			if("2".equals(direction)){
				Deal deal_usdt_qc = compService.compUsdCny(ab_usdt, ab_qc);
				orderService.dealUsdt2Qc(deal_usdt_qc, ai);
			}
			if("0".equals(direction)){
				Deal deal_ac_usdt = compService.compCnyUsd(ab_qc, ab_usdt);//cny转usd
				orderService.dealQc2Usdt(deal_ac_usdt, ai);
				
				Deal deal_usdt_qc = compService.compUsdCny(ab_usdt, ab_qc);
				orderService.dealUsdt2Qc(deal_usdt_qc, ai);
			}
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
