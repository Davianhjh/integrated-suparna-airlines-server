package com.agiview.control;

import java.io.Serializable;

public class auctionManageParam implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String flightNo;
	private String date;
	
	public auctionManageParam() {
		super();
	}

	public String getFlightNo() {
		return flightNo;
	}
	
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
}
