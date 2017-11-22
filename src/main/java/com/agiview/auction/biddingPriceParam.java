package com.agiview.auction;

import java.io.Serializable;

public class biddingPriceParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String auctionID;
	private int price;
	
	public biddingPriceParam() {
		super();
	}
	
	public String getAuctionID() {
		return auctionID;
	}
	
	public void setAuctionID(String auctionid) {
		this.auctionID = auctionid;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
}
