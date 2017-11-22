package com.agiview.auction;

import java.io.Serializable;

public class issueTicketParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String auctionID;
	
	public issueTicketParam() {
		super();
	}
	
	public String getAuctionID() {
		return auctionID;
	}
	
	public void setAuctionID(String auctionid) {
		this.auctionID = auctionid;
	}
}
