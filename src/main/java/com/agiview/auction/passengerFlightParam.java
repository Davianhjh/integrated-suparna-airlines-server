package com.agiview.auction;

import java.io.Serializable;

public class passengerFlightParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String flightNo;
	private String auctionID;
	
	public passengerFlightParam() {
		super();
	}
	
	public String getFlightNo() {
		return flightNo;
	}
	
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	
	public String getAuctionID() {
		return auctionID;
	}
	
	public void setAuctionID(String auctionid) {
		this.auctionID = auctionid;
	}
}

