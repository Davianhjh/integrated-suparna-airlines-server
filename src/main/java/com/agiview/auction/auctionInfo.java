package com.agiview.auction;

public class auctionInfo {
	
	private String auctionID;
	private int auctionType;
	private int auctionState;
	private int startTime;
	private int endTime;
	private int timeLeft;
	private int userStatus;
	private String description;
	//private int price;
	private int hit;
	
	public auctionInfo() {
		super();
	}
	
	public String getAuctionID() {
		return auctionID;
	}
	
	public void setAuctionID(String auctionid) {
		this.auctionID = auctionid;
	}
	
	public int getAuctionType() {
		return auctionType;
	}
	
	public void setAuctionType(int type) {
		this.auctionType = type;
	}
	
	public int getAuctionState() {
		return auctionState;
	}
	
	public void setAuctionState(int auctionState) {
		this.auctionState = auctionState;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	
	public int getEndTime() {
		return endTime;
	}
	
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public void setTimeLeft(int timeleft) {
		this.timeLeft = timeleft;
	}
	
	public int getUserStatus() {
		return userStatus;
	}
	
	public void setUserStatus(int userstatus) {
		this.userStatus = userstatus;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String dscp) {
		this.description = dscp;
	}
	/*
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	*/
	
	public int getHit() {
		return hit;
	}
	
	public void setHit(int hit) {
		this.hit = hit;
	}
}
