package com.agiview.auction;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
import com.agiview.control.changeAuctionState;
import com.agiview.member.tokenHandler;

@Path("/biddingAgree")  
public class biddingAgree {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public biddingAgreeRes agree(@Context HttpHeaders hh, biddingAgreeParam ba) throws SQLException {
		/*
		Cookie tmp1 = hh.getCookies().get("token");
		Cookie tmp2 = hh.getCookies().get("uuid");
    	String AgiToken = (tmp1 == null) ? null : tmp1.getValue();
    	String uuid = (tmp2 == null) ? null : tmp2.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String uuid = header.getFirst("uuid");
    	biddingAgreeRes res = new biddingAgreeRes();
    	Connection conn = null;
    	String memberIDcard = null;
    	if(ba == null) {
    		res.setAuth(-1); 
    		res.setAgree(-1);
            res.setCode(1000);                   // parameter not found
            return res;
    	}
    	String auctionID = ba.getAuctionID();
    	
    	if (auctionID == null) {
    		res.setAuth(-1); 
    		res.setAgree(-1);
            res.setCode(1000);                   // parameter error
            return res;
    	}
    	
    	try {
    		conn = HiKariCPHandler.getConn(); 
    	} catch (SQLException e) {
    		res.setAuth(-1); 
    		res.setAgree(-1);
            res.setCode(2000);                   // connection failed to get
            return res;
    	}
    	
    	if(AgiToken == null && uuid == null) {
    		res.setAuth(-1); 
    		res.setAgree(-1);
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
    	        		memberIDcard = tokenHandler.parseJWT(AgiToken);
    	        	} catch (Exception e) {
    	    			e.printStackTrace();
    	    			res.setAuth(-1); 
    	        		res.setAgree(-1);
    	                res.setCode(1020);       // token expired
    	                conn.close();
    	                return res;
    	        	}
    	        	try {
    	    			int verifyAuctionState = changeAuctionState.verifyAuctionState(conn, auctionID);
    	    			if(verifyAuctionState == 1) {
    	    				saveBiddingAgree(conn, auctionID, memberIDcard);
    	        			res.setAuth(1); 
    	            		res.setAgree(1);
    	    			}
    	    			else {
    	    				res.setAuth(1); 
    	            		res.setAgree(-1);
    	            		res.setCode(1040);    // auction not started OR is over
    	    			}
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	        		res.setAgree(-1);
    	                res.setCode(2000);        // saving biddingAgree failed
    	        		conn.close();
    	        		return res;
    	    		}
    	        }
    	        else {
    	        	res.setAuth(-1); 
            		res.setAgree(-1);
                    res.setCode(1020);            // token not match
    	        }
        		ret.close();
        		pst.close();
                conn.close();
                return res;
    		} catch (SQLException e) {
    			e.printStackTrace();
    			res.setAuth(-1); 
        		res.setAgree(-1);
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
    	        	String IDcard = ret.getString(1);
    	        	try {
    	        		int verifyAuctionState = changeAuctionState.verifyAuctionState(conn, auctionID);
    	        		if(verifyAuctionState == 1) {
    	    				saveBiddingAgree(conn, auctionID, IDcard);
    	        			res.setAuth(1); 
    	            		res.setAgree(1);
    	    			}
    	    			else {
    	    				res.setAuth(1); 
    	            		res.setAgree(-1);
    	            		res.setCode(1040);    // auction not started OR is over
    	    			}

    	        	} catch (SQLException e) {
    	        		res.setAuth(1);
    	        		res.setAgree(-1);
    	        		res.setCode(2000);        // saving biddingAgree failed
    	        		conn.close();
    	        		return res;
    	        	}
    	        }
    	        else {
    	        	res.setAuth(-1); 
            		res.setAgree(-1);
                    res.setCode(1020);            // uuid not match
    	        }
        		ret.close();
        		pst.close();
        		conn.close();
        		return res;
    	    	
    		} catch (SQLException e){
    			e.printStackTrace();
    			res.setAuth(-1); 
        		res.setAgree(-1);
                res.setCode(2000);      // verify uuid failed
                conn.close();
                return res;
    		}
    	}
    	else {
    		res.setAuth(-1); 
    		res.setAgree(-1);
            res.setCode(1000);          // parameter redundant
        	conn.close();
        	return res;
    	}
	}
	
	public void saveBiddingAgree(Connection conn, String auctionID, String identity) throws SQLException {
    	PreparedStatement pst = null;
    	ResultSet ret = null;
    	String sql1 = "SELECT userStatus FROM userStates WHERE auctionID=? AND identity=?;";
    	
        pst = conn.prepareStatement(sql1);
        pst.setString(1, auctionID);
        pst.setString(2, identity);
        ret = pst.executeQuery();
        if(ret.next()) {
        	int userstatus = ret.getInt(1);
        	if(userstatus != 1) {
        		ret.close();
        		pst.close();
        		String sql2 = "UPDATE userStates set userStatus=?, timeStamp=? WHERE auctionID=? AND identity=?;";
        		java.util.Date date = new java.util.Date();
    	        Timestamp timeStamp = new Timestamp(date.getTime());

        		pst = conn.prepareStatement(sql2);
        		pst.setInt(1, 1);
        		pst.setTimestamp(2, timeStamp);
                pst.setString(3, auctionID);
                pst.setString(4, identity);
                pst.executeUpdate();
        	}
        }
	}
	
	public class biddingAgreeRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	    
	    private int auth;
	    private int agree;
	    private int code;
	    
	    public biddingAgreeRes() {
	    	super();
	    }
	    
	    public int getAuth() {
	    	return auth;
	    }
	    
	    public void setAuth(int auth) {
	    	this.auth = auth;
	    }
	    
	    public int getAgree() {
	    	return agree;
	    }
	    
	    public void setAgree(int agree) {
	    	this.agree = agree;
	    }
	    
	    public int getCode() {
	    	return code;
	    }
	    
	    public void setCode(int code) {
	    	this.code = code;
	    }
	}
}
