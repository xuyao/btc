package cn.xy.okex.vo;

public class Order {
	
	private long order_id;
	
	private int status;
	
	private String symbol;
	
	private String type;
	
	private double price;
	
	private double amount;
	
	private double deal_amount;
	
	private double avg_price;

	public long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(long order_id) {
		this.order_id = order_id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getDeal_amount() {
		return deal_amount;
	}

	public void setDeal_amount(double deal_amount) {
		this.deal_amount = deal_amount;
	}

	public double getAvg_price() {
		return avg_price;
	}

	public void setAvg_price(double avg_price) {
		this.avg_price = avg_price;
	}
	
	
}
