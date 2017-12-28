package cn.xy.exx.vo;

public class Deal {

	private String buyMarket;
	private Double buyPrice;
	private Double buyAmount;
	
	private String sellMarket;
	private Double sellPrice;
	private Double sellAmount;

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

	public Double getBuyAmount() {
		return buyAmount;
	}

	public void setBuyAmount(Double buyAmount) {
		this.buyAmount = buyAmount;
	}

	public Double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Double getSellAmount() {
		return sellAmount;
	}

	public void setSellAmount(Double sellAmount) {
		this.sellAmount = sellAmount;
	}

}
