package com.agiview.auction;

import java.io.Serializable;

public class timeRemainParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String auctionID;
	
	public timeRemainParam() {
		super();
	}
	
	public String getAuctionID() {
		return auctionID;
	}
	
	public void setAuctionID(String auctionid) {
		this.auctionID = auctionid;
	}
}