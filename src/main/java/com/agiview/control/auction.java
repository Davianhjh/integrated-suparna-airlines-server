package com.agiview.control;

public class auction {
	
	private String auctionID;
	private int auctionType;
	private int stageType;
	private int basePrice;
	private int startTime;
	private int endTime;
	private int timeLeft;
	private int msgSwitch;
	private int auctionState;
	private String description;
	private int advancedHours;
	private String operator;
	
	public auction() {
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
	
	public int getStageType() {
		return stageType;
	}
	
	public void setStageType(int stage) {
		this.stageType = stage;
	}
	
	public int getBasePrice() {
		return basePrice;
	}
	
	public void setBasePrice(int price) {
		this.basePrice = price;
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
	
	public int getMsgSwitch() {
		return msgSwitch;
	}
	
	public void setMsgSwitch(int msgSwitch) {
		this.msgSwitch = msgSwitch;
	}
	
	public int getAuctionState() {
		return auctionState;
	}
	
	public void setAuctionState(int auctionState) {
		this.auctionState = auctionState;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String dscp) {
		this.description = dscp;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String person) {
		this.operator = person;
	}
	
	public int getAdvancedHours() {
		return advancedHours;
	}
	
	public void setAdvancedHours(int hour) {
		this.advancedHours = hour;
	}
}
