package cn.xy.btc.vo;

/**
 * 	买卖价格和数量
 * */
public class AskBid {
	private String market;//市场
	private Double ask1;//卖1价格
	private Double ask2;//卖2价格
	private Integer ask1_amount;//卖1数量
	private Integer ask2_amount;//卖2数量
	
	private Double bid1;//买1价格
	private Double bid2;//买2价格
	private Integer bid1_amount;//买1数量
	private Integer bid2_amount;//买2数量
	
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public Double getAsk1() {
		return ask1;
	}
	public void setAsk1(Double ask1) {
		this.ask1 = ask1;
	}
	public Double getAsk2() {
		return ask2;
	}
	public void setAsk2(Double ask2) {
		this.ask2 = ask2;
	}
	public Integer getAsk1_amount() {
		return ask1_amount;
	}
	public void setAsk1_amount(Integer ask1_amount) {
		this.ask1_amount = ask1_amount;
	}
	public Integer getAsk2_amount() {
		return ask2_amount;
	}
	public void setAsk2_amount(Integer ask2_amount) {
		this.ask2_amount = ask2_amount;
	}
	public Double getBid1() {
		return bid1;
	}
	public void setBid1(Double bid1) {
		this.bid1 = bid1;
	}
	public Double getBid2() {
		return bid2;
	}
	public void setBid2(Double bid2) {
		this.bid2 = bid2;
	}
	public Integer getBid1_amount() {
		return bid1_amount;
	}
	public void setBid1_amount(Integer bid1_amount) {
		this.bid1_amount = bid1_amount;
	}
	public Integer getBid2_amount() {
		return bid2_amount;
	}
	public void setBid2_amount(Integer bid2_amount) {
		this.bid2_amount = bid2_amount;
	}
}
