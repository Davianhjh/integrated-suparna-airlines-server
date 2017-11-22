package com.agiview.auction;

import java.io.Serializable;

public class biddingAgreeParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String auctionID;
	private String ticketUUID;
	
	public biddingAgreeParam() {
		super();
	}
	
	public String getAuctionID() {
		return auctionID;
	}
	
	public void setAuctionID(String auctionid) {
		this.auctionID = auctionid;
	}
	
	public String getTicketUUID() {
		return ticketUUID;
	}
	
	public void setTicketUUID(String ticketUUID) {
		this.ticketUUID = ticketUUID;
	}
}