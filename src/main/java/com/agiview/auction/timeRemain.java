package com.agiview.auction;

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

@Path("/timeRemain")  
public class timeRemain {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public timeRemainRes time(@Context HttpHeaders hh, timeRemainParam tr) throws Exception {
		/*
		Cookie tmp1 = hh.getCookies().get("token");
		Cookie tmp2 = hh.getCookies().get("uuid");
    	String AgiToken = (tmp1 == null) ? null : tmp1.getValue();
    	String uuid = (tmp2 == null) ? null : tmp2.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String uuid = header.getFirst("uuid");
    	timeRemainRes res = new timeRemainRes();
    	Connection conn = null;
    	if(tr == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                   // parameter not found
            return res;
    	}
    	String auctionID = tr.getAuctionID();
    	
    	if(auctionID == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                   // parameter error
            return res;
    	}
    	
    	try {
    		conn = HiKariCPHandler.getConn(); 
    	} catch (SQLException e) {
    		res.setAuth(-1); 
            res.setCode(2000);                   // connection failed to get
            return res;
    	}
    	
    	if(AgiToken == null && uuid == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                   // parameter not found
            conn.close();
            return res;
    	}
    	else if(AgiToken != null && uuid == null) {
    		try {
    			String sql = "SELECT token from userToken where token=?;";
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
    	                res.setCode(1020);                // token expired
    	    		}
    	    		try {
    	    			timeLeft tl = getTimeLeft(conn, auctionID);
    	    			res.setAuth(1); 
    	    			res.setTimeLeftHours(tl.getTimeLeftHours());      // if timeLeft is -1, auction is finished
    	    			res.setEndTimeStamp(tl.getEndTimeStamp());
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	                res.setCode(2000);                // getting timeLeft failed
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
    	else if(AgiToken == null && uuid != null) {
    		try {
    			String sql = "SELECT IDcard from userToken where uuid=?;";
    	    	PreparedStatement pst = null;
    	    	ResultSet ret= null;
    	        pst = conn.prepareStatement(sql);
    	        pst.setString(1, uuid);
    	        ret = pst.executeQuery();
    	        if(ret.next()) {
    	        	timeLeft tl = getTimeLeft(conn, auctionID);
	    	        res.setAuth(1); 
	    	        res.setTimeLeftHours(tl.getTimeLeftHours());  // if timeLeft is -1, auction is finished
	    	        res.setEndTimeStamp(tl.getEndTimeStamp());
    	        }
    	        else {
    	        	res.setAuth(-1); 
                    res.setCode(1020);     // uuid not match
    	        }
        		ret.close();
        		pst.close();
        		conn.close();
                return res;
    	    	
    		} catch (SQLException e){
    			e.printStackTrace();
    			res.setAuth(-1); 
                res.setCode(2000);         // uuid verify failed
                conn.close();
                return res;
    		}
    	}
    	else {
    		res.setAuth(-1); 
            res.setCode(1000);             // parameter redundant
            conn.close();
            return res;
    	}
	}
	
	public timeLeft getTimeLeft(Connection conn, String auctionID) throws SQLException {
		String sql = "SELECT endTime from auctionParam where auctionID=?;";
    	PreparedStatement pst = null;
    	ResultSet ret= null;
    	timeLeft tl = new timeLeft();
    	
        pst = conn.prepareStatement(sql);
        pst.setString(1, auctionID);
        ret = pst.executeQuery();
        if(ret.next()) {
        	Timestamp endTime = ret.getTimestamp(1);
        	java.util.Date date = new java.util.Date();
            Timestamp nowTime = new Timestamp(date.getTime());            
            SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
            long t_end,t_now;
            try {
            	t_end = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
            	t_now = timeformat.parse(getTimeStampNumberFormat(nowTime)).getTime();
            	tl.setTimeLeftHours((int) ((t_end - t_now) / (3600 * 1000)));
            	tl.setEndTimeStamp((int) (t_end / 1000));
            } catch (ParseException e) {
            	e.printStackTrace();
            	return tl;
            }
        }
        return tl;        	
	}
	
	public static String getTimeStampNumberFormat(Timestamp formatTime) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(formatTime);
    }
	
	public class timeLeft {
		private int timeLeftHours;
		private int endTimeStamp;
		
		public timeLeft() {
			this.timeLeftHours = 0;
			this.endTimeStamp = 0;
		}
		
		public int getEndTimeStamp() {
	    	return endTimeStamp;
	    }
	    
	    public void setEndTimeStamp(int endTime) {
	    	this.endTimeStamp = endTime;
	    }
	    
	    public int getTimeLeftHours() {
	    	return timeLeftHours;
	    }
	    
	    public void setTimeLeftHours(int timeLeft) {
	    	this.timeLeftHours = timeLeft;
	    }
	}
	
	public class timeRemainRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	    
	    private int auth;
	    private int code;
	    private int timeLeftHours;
	    private int endTimeStamp;
	    
	    public timeRemainRes() {
	    	super();
	    }
	    
	    public int getAuth() {
	    	return auth;
	    }
	    
	    public void setAuth(int auth) {
	    	this.auth = auth;
	    }
	    
	    public int getCode() {
	    	return code;
	    }
	    
	    public void setCode(int code) {
	    	this.code = code;
	    }
	    
	    public int getTimeLeftHours() {
	    	return timeLeftHours;
	    }
	    
	    public void setTimeLeftHours(int t) {
	    	this.timeLeftHours = t;
	    }
	    
	    public int getEndTimeStamp() {
	    	return endTimeStamp;
	    }
	    
	    public void setEndTimeStamp(int endTime) {
	    	this.endTimeStamp = endTime;
	    }
	}
}