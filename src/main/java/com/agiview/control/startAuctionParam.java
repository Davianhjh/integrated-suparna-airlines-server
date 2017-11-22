package com.agiview.control;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.agiview.HiKariCP.HiKariCPHandler;

public class startAuctionParam implements Serializable {  
    private static final long serialVersionUID = 1L;

    private String auctionID;    // generated
    private String flightNo;
    private String airlineCode;
    private String date;
    private String orgCity;
    private String dstCity;
    private Timestamp depTime;
    private Timestamp arrTime;
    private int auctionType;
    private int basePrice;
    private int stageType;
    private Timestamp startTime;
    private Timestamp endTime;
    private int msgSwitch;
    private String description;
    private int advancedHours;
    
    public startAuctionParam(String flightNo, String airlineCode, String date, String orgCity, String dstCity,
    		Timestamp depTime, Timestamp arrTime, int auctionType, int basePrice, int stageType,
    		Timestamp startTime, Timestamp endTime, int msgSwitch, String description, int advancedHours) {
    	this.flightNo = flightNo;
    	this.airlineCode = airlineCode;
    	this.date = date;
    	this.orgCity = orgCity;
    	this.dstCity = dstCity;
    	this.depTime = depTime;
    	this.arrTime = arrTime;
    	this.auctionType = auctionType;
    	this.basePrice = basePrice;
    	this.stageType = stageType;
    	this.startTime = startTime;
    	this.endTime = endTime;
    	this.msgSwitch = msgSwitch;
    	this.description = description;
    	this.advancedHours = advancedHours;
    	setAuctionID();
    }
    
    public int registeAuction(Connection conn, String operator) throws SQLException {
    	PreparedStatement pst = null;
    	ResultSet ret = null;
    	conn = HiKariCPHandler.getConn();
    	
    	String sql = "SELECT flightNo FROM auctionManage WHERE auctionID=?;";
    	pst = conn.prepareStatement(sql);
    	pst.setString(1, auctionID);
    	ret = pst.executeQuery();
    	if(ret.next()) {
    		return 0;
    	}
    	else {	
    		ret.close();
    		pst.close();
	    	String sql1 = "INSERT INTO auctionManage (auctionID, flightNo, airlineCode, date, orgCity, dstCity, depTime, "
	    			+ "arrTime, operator) VALUES (?,?,?,?,?,?,?,?,?);";
	    	 
	        pst = conn.prepareStatement(sql1);
	        pst.setString(1, auctionID);
	        pst.setString(2, flightNo);
	        pst.setString(3, airlineCode);
	        pst.setString(4, date);
	        pst.setString(5, orgCity);
	        pst.setString(6, dstCity);
	        pst.setTimestamp(7, depTime);
	        pst.setTimestamp(8, arrTime);
	        pst.setString(9, operator);
	        pst.executeUpdate();
	        
	        pst.close();
	        String sql2 = "INSERT INTO auctionParam (auctionID, flightNo, flightDate, auctionType, stageType, basePrice, startTime, endTime, "
	        		+ "msgSwitch, auctionState, description, advancedHours, operator) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";
	        
	        pst = conn.prepareStatement(sql2);
	        pst.setString(1, auctionID);
	        pst.setString(2, flightNo);
	        pst.setString(3, date);
	        pst.setInt(4, auctionType);
	        pst.setInt(5, stageType);
	        pst.setInt(6, basePrice);
	        pst.setTimestamp(7, startTime);
	        pst.setTimestamp(8, endTime);
	        pst.setInt(9, msgSwitch);
	        pst.setInt(10, 1);
	        pst.setString(11, description);
	        pst.setInt(12, advancedHours);
	        pst.setString(13, operator);
	        pst.executeUpdate();       

	        SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
	        try {
	        	System.out.println(auctionID + " start");
	        	System.out.println(startTime);
		        long t_end = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
		    	long t_now = timeformat.parse(getTimeStampNumberFormat(startTime)).getTime();
		    	long tmp = 60*30;
		        startAuction sa = new startAuction();
		    	return sa.auctionStart(auctionID, tmp);      //start auctionTimer
	        } catch (ParseException e) {
	        	e.printStackTrace();
	        	return -1;
	        }
    	}
    }
    
	public static String getTimeStampNumberFormat(Timestamp formatTime) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(formatTime);
    }
    
    public void setAuctionID() {
    	String typeStr = "TYPE" + this.auctionType;
    	String stageStr = "S" + this.stageType;
    	String dateStr = String.join("",this.date.split("-"));
    	this.auctionID = dateStr + this.flightNo + typeStr + stageStr;
    }
    
    public String getAuctionID() {
    	return auctionID;
    }
    
    /*
    public void setFlightNo(String flightNo) {
    	this.flightNo = flightNo;
    }
    
    public String getFlightNo() {
    	return flightNo;
    }
    
    public void setDate(String date) {
    	this.date = date;
    }
    
    public String getDate() {
    	return date;
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
    
    public void setDepTime(Timestamp dep) {
    	this.depTime = dep;
    }
    
    public Timestamp getDepTime() {
    	return depTime;
    }
    
    public void setArrTime(Timestamp arr) {
    	this.arrTime = arr;
    }
    
    public Timestamp getArrTime() {
    	return arrTime;
    }
    
    public void setAuctionType(int type) {
    	this.auctionType = type;
    }
    
    public int getAuctionType() {
    	return auctionType;
    }
    
    public void setBasePrice(int price){
    	this.basePrice = price;
    }
    
    public int getBasePrice() {
    	return basePrice;
    }
    
    public void setStageType(int stage) {
    	this.stageType = stage;
    }
    
    public int getStageType() {
    	return stageType;
    }
    
    public void setStartTime(Timestamp start) {
    	this.startTime = start;
    }
    
    public Timestamp getStartTime() {
    	return startTime;
    }
    
    public void setEndTime(Timestamp end) {
    	this.endTime = end;
    }
    
    public Timestamp getEndTime() {
    	return endTime;
    }
    */
}
