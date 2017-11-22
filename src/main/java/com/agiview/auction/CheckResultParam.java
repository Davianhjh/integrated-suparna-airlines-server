package com.agiview.auction;

import java.io.Serializable;

public class CheckResultParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String auctionID;
	
	public CheckResultParam() {
		super();
	}
	
	public String getAuctionID() {
		return auctionID;
	}
	
	public void setAuctionID(String auctionid) {
		this.auctionID = auctionid;
	}
}