package cn.xy.btc;

public class Market {

	public static String[][] arry = new String[13][];//13行n列
	
	public static void init(){
		arry[0] = new String[]{"btc_qc","btc_usdt",null};
		arry[1] = new String[]{"bcc_qc","bcc_usdt","bcc_btc"};
		arry[2] = new String[]{"ubtc_qc","ubtc_usdt","ubtc_btc"};
		arry[3] = new String[]{"ltc_qc","ltc_usdt","ltc_btc"};
		arry[4] = new String[]{"eth_qc","eth_usdt","eth_btc"};
		arry[5] = new String[]{"etc_qc","etc_usdt","etc_btc"};
		arry[6] = new String[]{"bts_qc","bts_usdt","bts_btc"};
		arry[7] = new String[]{"eos_qc","eos_usdt","eos_btc"};
		arry[8] = new String[]{"qtum_qc","qtum_usdt","qtum_btc"};
		arry[9] = new String[]{"hsr_qc","hsr_usdt","hsr_btc"};
		arry[10] = new String[]{"xrp_qc","xrp_usdt","xrp_btc"};
		arry[11] = new String[]{"bcd_qc","bcd_usdt","bcd_btc"};
		arry[12] = new String[]{"dash_qc","dash_usdt","dash_btc"};
	}

}
