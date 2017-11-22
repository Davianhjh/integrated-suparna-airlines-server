package com.agiview.control;

import java.io.Serializable;

public class reviseAuctionParam implements Serializable {  
    private static final long serialVersionUID = 1L;
    
    private String auctionID;
    private int basePrice;
    private int advancedHours;
    private int msgSwitch;
    private String description;
    
    public reviseAuctionParam () {
    	super();
    }
    
    public String getAuctionID() {
    	return auctionID;
    }
    
    public void setAuctionID(String auction) {
    	this.auctionID = auction;
    }
    
    public int getBasePrice() {
    	return basePrice;
    }
    
    public void setBasePrice(int price) {
    	this.basePrice = price;
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
