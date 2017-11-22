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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.agiview.HiKariCP.HiKariCPHandler;
import com.agiview.auction.auctionScheduleList;
import com.agiview.control.startAuction.auctionTask;
import com.agiview.member.tokenHandler;

@Path("/reviseAuction")  
public class reviseAuction {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public reviseAuctionRes revise (@Context HttpHeaders hh, reviseAuctionParam ra) throws SQLException {
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	reviseAuctionRes res = new reviseAuctionRes();
    	Connection conn = null;
    	String admin = null;
    	if(ra == null) {
    		res.setAuth(-1); 
    		res.setStatus(-1);
            res.setCode(1000);                        // parameter not found
            return res;
    	}
    	String auctionID = ra.getAuctionID();
    	int basePrice = ra.getBasePrice();
    	int advancedHours = ra.getAdvancedHours();
    	int msgSwitch = ra.getMsgSwitch();
    	String description = ra.getDescription();
    	
    	if(auctionID == null || basePrice == 0 || advancedHours == 0 || msgSwitch == 0 || description == null) {
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
	    	        	res.setAuth(1);
	    	        	int verifyAuctionState = changeAuctionState.verifyAuctionState(conn, auctionID);
	    	        	if(verifyAuctionState == 1) {         // auction is still on
		    	        	Timestamp new_end = getEndTime(conn, auctionID, advancedHours);
		    	        	if(new_end != null) {
			    	        	reviseAuctionParam(conn, auctionID, basePrice, new_end, msgSwitch, description, advancedHours);
			    	        	rescheduleAuction(conn, auctionID);
			    	        	res.setStatus(1);
		    	        	}
		    	        	else {
	 	    	        		res.setStatus(-1);
	 	    	        		res.setCode(1040);         // auctionID not found or TIME exceeded
		    	        	}
	    	        	}
	    	        	else {
 	    	        		res.setStatus(-1);
 	    	        		res.setCode(1030);         // auctionID is over
	    	        	}
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	        		res.setStatus(-1);
    	                res.setCode(2000);             // updating auction failed / auction is over
    	                conn.close();
    	                return res;
    	    		}
    	        }
    	        else {
    	        	res.setAuth(-1); 
 	        		res.setStatus(-1);
                    res.setCode(1020);            // token not match
    	        }
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
	
	public Timestamp getEndTime(Connection conn, String auctionID, int advancedHours) throws SQLException {
    	PreparedStatement pst = null;
    	ResultSet ret = null;
    	String sql = "SELECT depTime FROM auctionManage WHERE auctionID=?";
    	
    	pst = conn.prepareStatement(sql);
    	pst.setString(1, auctionID);
    	ret = pst.executeQuery();
    	if(ret.next()) {
    		Timestamp depTime = ret.getTimestamp(1);
            SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
            long d_time, e_time;
            try {
            	d_time = timeformat.parse(getTimeStampNumberFormat(depTime)).getTime();
            	e_time = d_time - advancedHours * 3600 * 1000;
            	if(e_time < 0)
            		return null;
            	else return new Timestamp(e_time);
            } catch (ParseException e) {
            	e.printStackTrace();
            	return null;
            }
    	}
    	else return null;
	}
	
	public void reviseAuctionParam(Connection conn, String auctionID, int basePrice, Timestamp new_end, int msgSwitch, String description, int advancedHours) throws SQLException {
    	PreparedStatement pst = null;
    	String sql = "UPDATE auctionParam set basePrice=?, endTime=?, msgSwitch=?, description=?, advancedHours=? WHERE auctionID=?;";
    	
    	pst = conn.prepareStatement(sql);
    	pst.setInt(1, basePrice);
    	pst.setTimestamp(2, new_end);
    	pst.setInt(3, msgSwitch);
    	pst.setString(4, description);
    	pst.setInt(5, advancedHours);
    	pst.setString(6, auctionID);
    	pst.executeUpdate();
	}
	
	public void rescheduleAuction(Connection conn, String auctionID) throws SQLException {
		ScheduledFuture target = auctionScheduleList.auctionSchedule.get(auctionID);
		if(target == null)
			throw new SQLException();
		target.cancel(true);        
		auctionScheduleList.auctionSchedule.remove(auctionID);     // cancel & remove the previous one;
		
		PreparedStatement pst = null;
		ResultSet ret = null;
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
		String sql = "SELECT endTime FROM auctionParam WHERE auctionID=?;";
		
		pst = conn.prepareStatement(sql);
		pst.setString(1, auctionID);
		ret = pst.executeQuery();
		if(ret.next()) {
        	java.util.Date date = new java.util.Date();
            Timestamp nowTime = new Timestamp(date.getTime()); 
			Timestamp endTime = ret.getTimestamp(1);
			long now_t, end_t, timeDelay;
			try {
            	now_t = timeformat.parse(getTimeStampNumberFormat(nowTime)).getTime();
            	end_t = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
            	//timeDelay = (end_t - now_t) / 1000;
            	timeDelay = 30;
            	ScheduledFuture sfa = startAuction.ses.schedule(new auctionTask(auctionID), timeDelay, TimeUnit.SECONDS);
            	auctionScheduleList.auctionSchedule.put(auctionID, sfa);       // register the auction schedule task
            	System.out.println("rescheduled successfully");
			} catch (Exception e) {
            	e.printStackTrace();
            	throw new SQLException();
            }
		}
		else throw new SQLException();
	}
	
	public class reviseAuctionRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	    
	    private int auth;
	    private int status;
	    private int code;
	    
	    public reviseAuctionRes() {
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
