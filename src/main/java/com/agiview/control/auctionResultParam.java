package com.agiview.control;

import java.io.Serializable;

public class auctionResultParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String auctionID;
	private String flightNo;
	private String flightDate;
	private String auctionType;
	private String stageType;
	
	public auctionResultParam() {
		super();
	}
	
	public String getAuctionID() {
		return auctionID;
	}
	
	public void setAuctionID(String auctionid) {
		this.auctionID = auctionid;
	}
	
	public String getFlightNo() {
		return flightNo;
	}
	
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	
	public String getFlightDate() {
		return flightDate;
	}
	
	public void setFlightDate(String flightDate) {
		this.flightDate = flightDate;
	}
	
	public String getAuctionType() {
		return auctionType;
	}
	
	public void setAuctionType(String auctionType) {
		this.auctionType = auctionType;
	}
	
	public String getStageType() {
		return stageType;
	}
	
	public void setStageType(String stageType) {
		this.stageType = stageType;
	}
}
