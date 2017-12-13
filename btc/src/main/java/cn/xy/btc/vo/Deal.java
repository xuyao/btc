package cn.xy.btc.vo;

public class Deal {

	private String buyMarket;
	private Double buyPrice;
	private Integer buyAmount;
	
	private String sellMarket;
	private Double sellPrice;
	private Integer sellAmount;

	public String getBuyMarket() {
		return buyMarket;
	}

	public void setBuyMarket(String buyMarket) {
		this.buyMarket = buyMarket;
	}

	public String getSellMarket() {
		return sellMarket;
	}

	public void setSellMarket(String sellMarket) {
		this.sellMarket = sellMarket;
	}

	public Double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(Double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Integer getBuyAmount() {
		return buyAmount;
	}

	public void setBuyAmount(Integer buyAmount) {
		this.buyAmount = buyAmount;
	}

	public Double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Integer getSellAmount() {
		return sellAmount;
	}

	public void setSellAmount(Integer sellAmount) {
		this.sellAmount = sellAmount;
	}

}
