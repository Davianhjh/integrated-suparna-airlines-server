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

@Path("/setAuction")  
public class setAuction {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public auctionStartRes set(@Context HttpHeaders hh, setAuctionParam as) throws SQLException {
		/*
		Cookie tmp = hh.getCookies().get("token");
    	String AgiToken = (tmp == null) ? null : tmp.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	auctionStartRes res = new auctionStartRes();
    	Connection conn = null;
    	String admin = null;
    	if(as == null) {
    		res.setAuth(-1); 
    		res.setStatus(-1);
            res.setCode(1000);                        // parameter not found
            return res;
    	}
    	String flightNo = as.getFlightNo();
    	String flightDate = as.getFlightDate();
    	int auctionType = as.getAuctionType();
    	int basePrice = as.getBasePrice();
    	int stageType = as.getStageType();
    	int advancedHours = as.getAdvancedHours();
    	int msgSwitch = as.getMsgSwitch();
    	String description = as.getDescription();
    	
    	if(flightNo == null || flightDate == null || auctionType == 0 || basePrice == 0 || stageType == 0 || advancedHours == 0 || msgSwitch == 0 || description == null) {
    		res.setAuth(-1); 
    		res.setStatus(-1);
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
    		res.setStatus(-1);
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
    	    			admin = tokenHandler.parseJWT(AgiToken);
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    			res.setAuth(-1); 
    	        		res.setStatus(-1);
    	                res.setCode(1020);                    // token not match OR expired    
    	                conn.close();
    	                return res;
    	    		}
    	    		try {
    	    			String sql1 = "SELECT airlineCode, orgCity, dstCity, depTime, arrTime, state FROM flightManage WHERE flightNo=? and flightDate=?;";
    	    			
    	    	        pst = conn.prepareStatement(sql1);
    	    	        pst.setString(1, flightNo);
    	    	        pst.setString(2, flightDate);
    	    	        ret = pst.executeQuery();
    	    	        if(ret.next()) {
    	    	        	String airlineCode = ret.getString(1);
    	    	        	String orgCity = ret.getString(2);
    	    	        	String dstCity = ret.getString(3);
    	    	        	Timestamp depTime = ret.getTimestamp(4);
    	    	        	Timestamp arrTime = ret.getTimestamp(5);
    	    	        	int state = ret.getInt(6);
    	    	        	
    	    	        	ret.close();
    	    	        	pst.close();
    	    	        	java.util.Date nowDate = new java.util.Date();
    	    	            Timestamp startTime = new Timestamp(nowDate.getTime());
    	    	            SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
    	    	            long deadline,startLine;
    	    	            try {
    	    	            	deadline = timeformat.parse(getTimeStampNumberFormat(depTime)).getTime();
    	    	            	startLine = timeformat.parse(getTimeStampNumberFormat(startTime)).getTime();
    	    	            	if((deadline - startLine) < advancedHours*3600*1000) {
    	    	            		res.setAuth(1);
    		    	            	res.setStatus(-1);
    		    	            	res.setCode(1050);       // time exceeded
    		    	            	conn.close();
    	    	            		return res;
    	    	            	}
    	    	            	Timestamp endTime = new Timestamp(deadline - advancedHours*3600*1000);
    	    	            	
    	    	            	String sql2 = "UPDATE flightManage set state=? WHERE flightNo=? AND flightDate=?;";
    	    	            	pst = conn.prepareStatement(sql2);
    	    	            	pst.setInt(1, state+1);
    	    	            	pst.setString(2, flightNo);
    	    	            	pst.setString(3, flightDate);
    	    	            	pst.executeUpdate();
    	    	            	
    	    	            	startAuctionParam sa = new startAuctionParam(flightNo, airlineCode, flightDate, orgCity, dstCity, depTime, arrTime,
    	    	            			auctionType, basePrice, stageType, startTime, endTime, msgSwitch, description, advancedHours);
    	    	            	int result = sa.registeAuction(conn, admin);            //save data into auctionManage & auctionParam, and start TimerTask
    	    	            	if(result == 1) {
    		    	            	res.setAuth(1);
    		    	            	res.setStatus(1);
    	    	            	}
    	    	            	else if(result == 0) {
    	    	            		res.setAuth(1);
    		    	            	res.setStatus(-1);
    		    	            	res.setCode(1030);       // this type of auction has been set
    	    	            	}
    	    	            	else {
    	    	            		res.setAuth(1);
    		    	            	res.setStatus(-1);
    		    	            	res.setCode(2000);      // auctionStart failed
    	    	            	}
    	    	            	
    	    	            } catch (ParseException | SQLException e) {
    	    	            	e.printStackTrace();
    	    	            	res.setAuth(1);
    	    	            	res.setStatus(-1);
    	    	                res.setCode(2000);     // Timestamp parse failed OR registeAuction failed OR update flightManage failed
    	    	                conn.close();
    	    	                return res;
    	    	            }
    	    	        }
    	    	        else {
    	    	        	res.setAuth(1); 
    	            		res.setStatus(-1);
    	                    res.setCode(1040);         // the flight not found
    	    	        }
    	    	        
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	        		res.setStatus(-1);
    	                res.setCode(2000);             // getting flightManage failed
    	                conn.close();
    	                return res;
    	    		}
    	        }
    	        else {
    	        	res.setAuth(-1); 
	        		res.setStatus(-1);
                    res.setCode(1020);            // token not match
    	        }
        		ret.close();
        		pst.close();
                conn.close();
                return res;
    		} catch (SQLException e) {
    			e.printStackTrace();
    			res.setAuth(-1); 
        		res.setStatus(-1);
                res.setCode(2000);       // verify token failed
                conn.close();
                return res;
    		}
    	}
	}
	
	public static String getTimeStampNumberFormat(Timestamp formatTime) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(formatTime);
    }
	
	public class auctionStartRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	    
	    private int auth;
	    private int status;
	    private int code;
	    
	    public auctionStartRes() {
	    	super();
	    }
	    
	    public int getAuth() {
	    	return auth;
	    }
	    
	    public void setAuth(int auth) {
	    	this.auth = auth;
	    }
	    
	    public int getStatus() {
	    	return status;
	    }
	    
	    public void setStatus(int status) {
	    	this.status = status;
	    }
	    
	    public int getCode() {
	    	return code;
	    }
	    
	    public void setCode(int code) {
	    	this.code = code;
	    }
	}
}