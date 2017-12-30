package cn.xy.zb.vo;

public class Order {
	
	private String currency;//交易类型 market
	private String id;//委托挂单号
	private Double price;//单价
	private Integer status;//挂单状态(0：待成交,1：取消,2：交易完成,3：待成交未交易部份)
	private Double total_amount;//挂单总数量
	private Double trade_amount;//已成交数量
	private Integer trade_date;//委托时间
	private Double trade_money;//已成交总金额
	private Integer type;//挂单类型 1/0[buy/sell]
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Double getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(Double total_amount) {
		this.total_amount = total_amount;
	}
	public Double getTrade_amount() {
		return trade_amount;
	}
	public void setTrade_amount(Double trade_amount) {
		this.trade_amount = trade_amount;
	}
	public Integer getTrade_date() {
		return trade_date;
	}
	public void setTrade_date(Integer trade_date) {
		this.trade_date = trade_date;
	}
	public Double getTrade_money() {
		return trade_money;
	}
	public void setTrade_money(Double trade_money) {
		this.trade_money = trade_money;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	@Override
	public String toString(){
		return currency+" "+id+" "+price+" "+status+" "+total_amount+" "+
				trade_amount+" "+trade_date+" "+trade_money+ " "+type;
	}
	
}
