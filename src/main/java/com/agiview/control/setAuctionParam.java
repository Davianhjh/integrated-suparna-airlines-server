package com.agiview.control;

import java.io.Serializable;

public class setAuctionParam implements Serializable {  
    private static final long serialVersionUID = 1L;
    
    private String flightNo;
    private String flightDate;
    private int auctionType;
    private int basePrice;
    private int stageType;
    private int advancedHours;
    private int msgSwitch;
    private String description;
    
    public setAuctionParam () {
    	super();
    }
    
    public String getFlightNo() {
    	return flightNo;
    }
    
    public void setFlightNo(String flight) {
    	this.flightNo = flight;
    }
    
    public String getFlightDate() {
    	return flightDate;
    }
    
    public void setFlightDate(String date) {
    	this.flightDate = date;
    }
    
    public int getAuctionType() {
    	return auctionType;
    }
    
    public void setAuctionType(int type) {
    	this.auctionType = type;
    }
    
    public int getBasePrice() {
    	return basePrice;
    }
    
    public void setBasePrice(int price) {
    	this.basePrice = price;
    }
    
    public int getStageType() {
    	return stageType;
    }
    
    public void setStageType(int type) {
    	this.stageType = type;
    }
    
    public int getAdvancedHours() {
    	return advancedHours;
    }
    
    public void setAdvancedHours(int hours) {
    	this.advancedHours = hours;
    }
    
    public int getMsgSwitch() {
    	return msgSwitch;
    }
    
    public void setMsgSwitch(int msgSwitch) {
    	this.msgSwitch = msgSwitch;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public void setDescription(String dscp) {
    	this.description = dscp;
    }
}
