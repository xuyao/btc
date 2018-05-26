package cn.xy.zb.vo;

public class AccountInfo {

	Double qcAvailable;//qc的剩余
	
	Double usdtAvailable;//usdt的剩余
	
	Double qcfreez;//qc的冻结
	
	Double usdtfreez;//usdt的冻结

	public Double getQcfreez() {
		return qcfreez;
	}

	public void setQcfreez(Double qcfreez) {
		this.qcfreez = qcfreez;
	}

	public Double getUsdtfreez() {
		return usdtfreez;
	}

	public void setUsdtfreez(Double usdtfreez) {
		this.usdtfreez = usdtfreez;
	}

	public Double getQcAvailable() {
		return qcAvailable;
	}

	public void setQcAvailable(Double qcAvailable) {
		this.qcAvailable = qcAvailable;
	}

	public Double getUsdtAvailable() {
		return usdtAvailable;
	}

	public void setUsdtAvailable(Double usdtAvailable) {
		this.usdtAvailable = usdtAvailable;
	}
	
}
