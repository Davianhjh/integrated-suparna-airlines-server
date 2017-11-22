package com.agiview.control;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.agiview.HiKariCP.HiKariCPHandler;
import com.agiview.member.tokenHandler;

@Path("/auctionManage")  
public class auctionManage {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public auctionManageRes manage(@Context HttpHeaders hh, auctionManageParam al) throws SQLException {
		/*
		Cookie tmp = hh.getCookies().get("token");
    	String AgiToken = (tmp == null) ? null : tmp.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	auctionManageRes res = new auctionManageRes();
    	Connection conn = null;
    	if(al == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                        // parameter not found
            return res;
    	}
    	String flightNo = al.getFlightNo();
    	String date = al.getDate();
    	
    	if(flightNo == null || date == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                        // parameter error
            return res;
    	}
    	
    	try {
    		conn = HiKariCPHandler.getConn(); 
    	} catch (SQLException e) {
    		res.setAuth(-1); 
            res.setCode(2000);                   // connection failed to get
            return res;
    	}
    	
    	if(AgiToken == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                        // parameter not found
            conn.close();
            return res;
    	}
    	else {
    		try {
    			String sql = "SELECT token from adminToken where token=?;";
    	    	PreparedStatement pst = null;
    	    	ResultSet ret= null;
    	        pst = conn.prepareStatement(sql);
    	        pst.setString(1, AgiToken);
    	        ret = pst.executeQuery();
    	        if(ret.next()) { 
    	    		try {
    	    			tokenHandler.parseJWT(AgiToken);
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    			res.setAuth(-1); 
    	                res.setCode(1020);                    // token expired   
    	                conn.close();
    	                return res;
    	    		}
    	    		try {
    	    			res.setAuth(1);
    	    			getAuctionList(conn, flightNo, date, res);
    	    		} catch (SQLException | ParseException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1);
    	    			res.setCode(2000);                   //  server error
    	    		}
    	        }
    	        else {
    	        	res.setAuth(-1); 
                    res.setCode(1020);            // token not match
    	        }
        		ret.close();
        		pst.close();
                conn.close();
                return res;
    		} catch (SQLException e) {
    			e.printStackTrace();
    			res.setAuth(-1); 
                res.setCode(2000);       // verify token failed
                conn.close();
                return res;
    		}
    	}
	}
	
	public String getTimeStampNumberFormat(Timestamp formatTime) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(formatTime);
    }
	
	public void getAuctionList(Connection conn, String flightNo, String date, auctionManageRes res) throws SQLException, ParseException {
		PreparedStatement pst, pst2 = null;
    	ResultSet ret, ret2 = null;
    	ArrayList<auction> auctions = new ArrayList<auction>();
    	String auctionID = null;
    	SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
    	
    	String sql2 = "SELECT orgCity, dstCity, depTime, arrTime FROM flightManage WHERE flightNo=? AND flightDate=?;";
    	pst2 = conn.prepareStatement(sql2);
    	pst2.setString(1, flightNo);
    	pst2.setString(2, date);
    	ret2 = pst2.executeQuery();
    	if(ret2.next()) {
			res.setFlightNo(flightNo);
			res.setDate(date);
			res.setOrgCity(ret2.getString(1));
			res.setDstCity(ret2.getString(2));
			Timestamp depTime = ret2.getTimestamp(3);
    		Timestamp arrTime = ret2.getTimestamp(4);
    		long t_dep, t_arr;
        	t_dep = timeformat.parse(getTimeStampNumberFormat(depTime)).getTime();
        	t_arr = timeformat.parse(getTimeStampNumberFormat(arrTime)).getTime();
        	res.setDepTime((int) (t_dep / 1000));
        	res.setArrTime((int) (t_arr / 1000));
        	
        	String sql = "SELECT auctionID, auctionType, stageType, basePrice, startTime, endTime, msgSwitch, auctionState, description, "
        			+ "advancedHours, operator FROM auctionParam WHERE flightNo=? AND flightDate=?;";
        	pst = conn.prepareStatement(sql);
        	pst.setString(1, flightNo);
        	pst.setString(2, date);
        	ret = pst.executeQuery();
        	while(ret.next()) {
        		auction ac = new auction();
        		auctionID = ret.getString(1);
        		ac.setAuctionID(auctionID);
        		ac.setAuctionType(ret.getInt(2));
        		ac.setStageType(ret.getInt(3));
        		ac.setBasePrice(ret.getInt(4));
        		java.util.Date nowdate = new java.util.Date();
                Timestamp nowTime = new Timestamp(nowdate.getTime());
        		Timestamp startTime = ret.getTimestamp(5);
        		Timestamp endTime = ret.getTimestamp(6);
        		long t_start, t_end, t_now;
        		t_start = timeformat.parse(getTimeStampNumberFormat(startTime)).getTime();
        		t_end = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
        		t_now = timeformat.parse(getTimeStampNumberFormat(nowTime)).getTime();
        		int timeLeft = (int)((t_end - t_now) / 1000) > 0 ? (int)((t_end - t_now) / 1000):0;
        		ac.setStartTime((int) (t_start / 1000));
        		ac.setEndTime((int) (t_end / 1000));
        		ac.setTimeLeft(timeLeft);
    			ac.setMsgSwitch(ret.getInt(7));
    			ac.setAuctionState(ret.getInt(8));
    			ac.setDescription(ret.getString(9));
    			ac.setAdvancedHours(ret.getInt(10));
        		ac.setOperator(ret.getString(11));
        		auctions.add(ac);
        	}
        	res.setAuctions(auctions);
    	}
	}
	
	public class auctionManageRes implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private int auth;
		private int code;
		private String flightNo;
		private String date;
		private String orgCity;
		private String dstCity;
		private int depTime;
		private int arrTime;
		private ArrayList<auction> auctions;
		
		public auctionManageRes() {
			super();
		}
		
		public void setAuth(int auth) {
			this.auth = auth;
		}
		
		public int getAuth() {
			return auth;
		}
		
		public void setCode(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getFlightNo() {
			return flightNo;
		}
		
		public void setFlightNo(String flightNo) {
			this.flightNo = flightNo;
		}
		
		public String getDate() {
			return date;
		}
		
		public void setDate(String flightDate) {
			this.date = flightDate;
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
	    
	    public ArrayList<auction> getAuctions(){
			return auctions;
		}
		
		public void setAuctions(ArrayList<auction> auction) {
			this.auctions = new ArrayList<auction>(auction);
		}
	}
}
