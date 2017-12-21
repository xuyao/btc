package cn.xy.zb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.xy.zb.service.HttpService;
import cn.xy.zb.util.ConstsUtil;

public class Tax {
	
	//market,tax
	public static Map<String,Double> map = new HashMap<String,Double>();//map
	
	public static void init(){
		
		String taxpath = ConstsUtil.getValue("taxpath");
		String text ="";
		try {
			text = FileUtils.readFileToString(new File(taxpath), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String[] s1 = text.split(";");
		for(String s : s1){
			String[] tax = s.split(",");
			String market = tax[0].toLowerCase();
//			System.out.println(market+"_btc："+doTaxValue(tax[1]));
//			System.out.println(market+"_usdt："+doTaxValue(tax[2]));
//			System.out.println(market+"_qc："+doTaxValue(tax[3]));
			map.put(market+"_btc", doTaxValue(tax[1]));
			map.put(market+"_usdt", doTaxValue(tax[2]));
			map.put(market+"_qc", doTaxValue(tax[3]));
		}
		
		
		String[][] arry = Market.arry;
		for(String[] sa : arry){
			if(map.get(sa[0])==null || map.get(sa[1])==null){
				System.out.println("market is not match tax!!! please checkout!");
				System.exit(0);
			}
		}
	}
	
	private static Double doTaxValue(String s){
		if("免费".equals(s)){
			return 0d;
		}else if(s.contains("-")){
			return null;
		}else{//带着百分号的
			s = s.replaceAll("%", "");
			return Double.valueOf(s)*0.01;
		}
	}
	
	
		
	public static void main(String[] args){
//			HttpService http = new HttpService();
//			String html = http.get("https://www.zb.com/i/rate");
//			String path = ConstsUtil.getValue("taxpath");
//			
//			Document doc = Jsoup.parse(html);
//			Elements es = doc.getElementsByTag("table");
//			Elements trs = es.get(0).getElementsByTag("tr");
//			Iterator it = trs.iterator();
//			it.next();
//			StringBuilder sb = new StringBuilder();
//			while(it.hasNext()){
//				Element tr = (Element)it.next();
//				Elements tds = tr.getElementsByTag("td");
//				sb.append(tds.get(0).text()).append(",");
//				sb.append(tds.get(1).text()).append(",");
//				sb.append(tds.get(2).text()).append(",");
//				sb.append(tds.get(3).text()).append(";");
//			}
//		
//			try {
//				FileUtils.writeStringToFile(new File(path), sb.toString(), "utf-8");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			System.out.println("save tax ok!");
			
		
			Market.init();
			init();
//			String market = "btc_qc";
//			MarketAB mab = Market.map.get(market);
//			Double tax = Tax.map.get(market);
//			Double amount = 0.0004;
//			amount = amount*(1-tax);
//			amount = NumberUtil.formatDouble(amount, mab.getAmountScale());
//			System.out.println(amount);
		}
	
	
}
