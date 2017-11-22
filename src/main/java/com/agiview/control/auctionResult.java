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

@Path("/auctionResult")  
public class auctionResult {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public auctionResultRes result(@Context HttpHeaders hh, auctionResultParam as) throws SQLException {
		/*
		Cookie tmp = hh.getCookies().get("token");
    	String AgiToken = (tmp == null) ? null : tmp.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String auctionID = null;
    	// if not passed with auctionID, then need to use flightNo & flightDate & auctionType & stageType instead
    	//
    	auctionResultRes res = new auctionResultRes();
    	Connection conn = null;
    	if(as == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                        // parameter not found
            return res;
    	}
    	
    	auctionID = as.getAuctionID();
    	if(auctionID == null) {
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
    	    			registeWinners(conn, auctionID, res);
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1);
    	    			res.setCode(2000);                // sql server error
    	    			conn.close();
    	    			return res;
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
	
	public void registeWinners(Connection conn, String auctionID, auctionResultRes res) throws SQLException {
    	PreparedStatement pst = null;
    	ResultSet ret= null;
		String sql1 = "SELECT auctionParam.flightNo, date, orgCity, dstCity, depTime, arrTime, startTime, endTime, auctionType, "
				+ "stageType, basePrice, seatNo, msgSwitch, auctionState FROM auctionParam INNER JOIN auctionManage ON "
				+ "auctionParam.auctionID=auctionManage.auctionID WHERE auctionParam.auctionID=?;";
		
		pst = conn.prepareStatement(sql1);
		pst.setString(1, auctionID);
		ret = pst.executeQuery();
		if(ret.next()) {
			res.setFlightNo(ret.getString(1));
			res.setFlightDate(ret.getString(2));
			res.setOrgCity(ret.getString(3));
			res.setDstCity(ret.getString(4));
			Timestamp depTime = ret.getTimestamp(5);
			Timestamp arrTime = ret.getTimestamp(6);
			Timestamp startTime = ret.getTimestamp(7);
			Timestamp endTime = ret.getTimestamp(8);
			res.setAuctionType(ret.getInt(9));
			res.setStageType(ret.getInt(10));
			res.setBasePrice(ret.getInt(11));
			res.setSeatNo(ret.getInt(12));
			res.setMsgSwitch(ret.getInt(13));
			res.setAuctionState(ret.getInt(14));
			SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
			try {
				long t_dep = timeformat.parse(getTimeStampNumberFormat(depTime)).getTime();
				long t_arr = timeformat.parse(getTimeStampNumberFormat(arrTime)).getTime();
				long t_start = timeformat.parse(getTimeStampNumberFormat(startTime)).getTime();
				long t_end = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
				res.setDepTime((int) (t_dep / 1000));
				res.setArrTime((int) (t_arr / 1000));
				res.setStartTime((int) (t_start / 1000));
				res.setEndTime((int) (t_end / 1000));    					
			} catch (ParseException e) {
				e.printStackTrace();
				throw new SQLException();
			}
			ret.close();
			pst.close();
			String sql2 = "SELECT username, tel, IDcard, biddingPrice, `timeStamp` FROM auctionResult INNER JOIN userToken ON "
					+ "auctionResult.identity = userToken.IDcard WHERE auctionID=?;";
			
			pst = conn.prepareStatement(sql2);
			pst.setString(1, auctionID);
			ret = pst.executeQuery();
			ArrayList<Person> result = new ArrayList<Person>();
			if(ret.next()) {
				Person ps = new Person();
				ps.setName(ret.getString(1));
				ps.setTel(ret.getString(2));
				ps.setIDcard(ret.getString(3));
				ps.setBiddingPrice(ret.getInt(4));
				Timestamp biddingTime = ret.getTimestamp(5);
				try {
					long b_time = timeformat.parse(getTimeStampNumberFormat(biddingTime)).getTime();
					ps.setTimeStamp((int) (b_time / 1000));
				} catch (ParseException e) {
					e.printStackTrace();
					throw new SQLException();
				}
				result.add(ps);
			}
			res.setWinners(result);
		}
	}
	
	public static String getTimeStampNumberFormat(Timestamp formatTime) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(formatTime);
    }
	
	public class auctionResultRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	    
	    private int auth;
	    private int code;
		private String flightNo;
		private String flightDate;
	    private String orgCity;
	    private String dstCity;
	    private int depTime;
	    private int arrTime;
	    private int auctionState;
		private int auctionType;
		private int stageType;
		private int seatNo;
		private int basePrice;
		private int startTime;
		private int endTime;
		private int msgSwitch;
		private ArrayList<Person> winners;
		
		public auctionResultRes() {
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
		
		public String getFlightDate() {
			return flightDate;
		}
		
		public void setFlightDate(String flightDate) {
			this.flightDate = flightDate;
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
	    
	    public int getAuctionState() {
	    	return auctionState;
	    }
	    
	    public void setAuctionState(int auctionState) {
	    	this.auctionState = auctionState;
	    }
		
		public int getAuctionType() {
			return auctionType;
		}
		
		public void setAuctionType(int auctionType) {
			this.auctionType = auctionType;
		}
		
		public int getStageType() {
			return stageType;
		}
		
		public void setStageType(int stageType) {
			this.stageType = stageType;
		}
		
		public int getSeatNo() {
			return seatNo;
		}
		
		public void setSeatNo(int seatNo) {
			this.seatNo = seatNo;
		}
		
		public int getBasePrice() {
			return basePrice;
		}
		
		public void setBasePrice(int basePrice) {
			this.basePrice = basePrice;
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
		
		public int getMsgSwitch() {
			return msgSwitch;
		}
		
		public void setMsgSwitch(int msgSwitch) {
			this.msgSwitch = msgSwitch;
		}
		
		public ArrayList<Person> getWinners(){
			return winners;
		}
		
		public void setWinners(ArrayList<Person> pr) {
			this.winners = new ArrayList<Person>(pr);
		}
	}
	
	public class Person {
		private String name;
		private String tel;
		private String IDcard;
		private int biddingPrice;
		private int timeStamp;
		
		public Person() {
			super();
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getTel() {
			return tel;
		}
		
		public void setTel(String tel) {
			this.tel = tel;
		}
		
		public String getIDcard() {
			return IDcard;
		}
		
		public void setIDcard(String IDcard) {
			this.IDcard = IDcard;
		}
		
		public void setBiddingPrice(int price) {
			this.biddingPrice = price;
		}
		
		public int getBiddingPrice() {
			return biddingPrice;
		}
		
		public void setTimeStamp(int timeStamp) {
			this.timeStamp = timeStamp;
		}
		
		public int getTimeStamp() {
			return timeStamp;
		}
	}

}
