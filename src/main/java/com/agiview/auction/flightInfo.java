package com.agiview.auction;

import java.util.ArrayList;

public class flightInfo {
	
	private String flightNo;
	private String ticketNo;
	private String flightDate;
	private String carbin;
	private String seat;
	private String entrance;
	private String onBoard;
	private String orgCity;
	private String dstCity;
	private int depTime;
	private int arrTime;
	private ArrayList<auctionInfo> auction;
	
	public flightInfo() {
		super();
	}
	
	public String getFlightNo() {
		return flightNo;
	}
	
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	
	public String getTicketNo() {
		return ticketNo;
	}
	
	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}
	
	public String getFlightDate() {
		return flightDate;
	}
	
	public void setFlightDate(String flightDate) {
		this.flightDate = flightDate;
	}
	
	public String getCarbin() {
		return carbin;
	}
	
	public void setCarbin(String carbin) {
		this.carbin = carbin;
	}
	
	public String getSeat() {
		return seat;
	}
	
	public void setSeat(String seat) {
		this.seat = seat;
	}
	
	public String getEntrance() {
		return entrance;
	}
	
	public void setEntrance(String entrance) {
		this.entrance = entrance;
	}
	
	public String getOnBoard() {
		return onBoard;
	}
	
	public void setOnBoard(String onBoard) {
		this.onBoard = onBoard;
	}
	
	public void setOrgCity(String org) {
    	this.orgCity = org;
    }
    
    public String getOrgCity() {
    	return orgCity;
    }
    
    public void setDstCity(String dst) {
    	this.dstCity = dst;
    }
    
    public String getDstCity() {
    	return dstCity;
    }
    
    public void setDepTime(int depTime) {
    	this.depTime = depTime;
    }
    
    public int getDepTime() {
    	return depTime;
    }
    
    public void setArrTime(int arrTime) {
    	this.arrTime = arrTime;
    }
    
    public int getArrTime() {
    	return arrTime;
    }
    
	public ArrayList<auctionInfo> getAuction(){
		return auction;
	}
	
	public void setAuctions(ArrayList<auctionInfo> ai) {
		this.auction = new ArrayList<auctionInfo>(ai);
	}
}