package com.agiview.control;

import java.util.ArrayList;

public class flight {
	
	private String flightNo;
	private String flightDate;
	private String planeType;
	private String tripType;
	private String orgCity;
	private String dstCity;
	private int depTime;
	private int arrTime;
	private int state;
	private ArrayList<Percent> proceed;
	
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
	
	public String getPlaneType() {
		return planeType;
	}
	
	public void setPlaneType(String planeType) {
		this.planeType = planeType;
	}
	
	public String getTripType() {
		return tripType;
	}
	
	public void setTripType(String tripType) {
		this.tripType = tripType;
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
    
    public void setState(int state) {
    	this.state = state;
    }
    
    public int getState() {
    	return state;
    }
    
	public ArrayList<Percent> getPercent(){
		return proceed;
	}
	
	public void setPercent(ArrayList<Percent> per) {
		this.proceed = new ArrayList<Percent>(per);
	}
}
