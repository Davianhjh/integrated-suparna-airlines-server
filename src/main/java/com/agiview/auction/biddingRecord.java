package com.agiview.auction;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

@Path("/biddingRecord")  
public class biddingRecord {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public biddingRecordRes record(@Context HttpHeaders hh, biddingAgreeParam ba) throws SQLException {
		/*
		Cookie tmp1 = hh.getCookies().get("token");
		Cookie tmp2 = hh.getCookies().get("uuid");
    	String AgiToken = (tmp1 == null) ? null : tmp1.getValue();
    	String uuid = (tmp2 == null) ? null : tmp2.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String uuid = header.getFirst("uuid");
    	biddingRecordRes res = new biddingRecordRes();
    	Connection conn = null;
    	String memberIDcard = null;
    	if(ba == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                   // parameter not found
            return res;
    	}
    	String auctionID = ba.getAuctionID();

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
    	    			memberIDcard = tokenHandler.parseJWT(AgiToken);
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    			res.setAuth(-1); 
    	                res.setCode(1020);               // token expired
    	                conn.close();
    	                return res;
    	    		}
    	    		try {
    	    			int biddingPrice = getBiddingRecord(conn, auctionID, memberIDcard);
    	    			if(biddingPrice != 0) {
    		    			res.setAuth(1); 
    		        		res.setPrice(biddingPrice);
    	    			}
    	    			else {
    	    				res.setAuth(1); 
    		        		res.setCode(1010);           // Not Found the user's biddingRecord
    	    			}
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	                res.setCode(2000);               // getting biddingRecrod failed
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
    	        		int biddingPrice = getBiddingRecord(conn, auctionID, IDcard);
    	        		if(biddingPrice != 0) {
    	        			res.setAuth(1); 
        	        		res.setPrice(biddingPrice);
    	        		}
    	        		else {
    	        			res.setAuth(1); 
    		        		res.setCode(1010);           // Not Found the user's biddingRecord
    	        		}
    	        	} catch (SQLException e) {
    	        		res.setAuth(1);
    	        		res.setCode(2000);               // getting biddingRecrod failed
    	        		conn.close();
    	        		return res;
    	        	}
    	        }
    	        else {
    	        	res.setAuth(-1); 
                    res.setCode(1020);                   // uuid not match
    	        }
        		ret.close();
        		pst.close();
        		conn.close();
                return res;
    	    	
    		} catch (SQLException e){
    			e.printStackTrace();
    			res.setAuth(-1); 
                res.setCode(2000);                       // uuid verify failed
                conn.close();
                return res;
    		}
    	}
    	else {
    		res.setAuth(-1); 
            res.setCode(1000);                           // parameter redundant
            conn.close();
            return res;
    	}
	}
	
	public int getBiddingRecord(Connection conn, String auctionID, String identity) throws SQLException {
    	PreparedStatement pst = null; 
    	ResultSet ret= null;
    	int price = 0;
		String sql = "SELECT biddingPrice FROM biddingPrice WHERE auctionID=? AND identity=?;";
        pst = conn.prepareStatement(sql);
        pst.setString(1, auctionID);
        pst.setString(2, identity);
        ret = pst.executeQuery();
        if(ret.next()) {
        	price = ret.getInt(1);
        }
		return price;
	}
	
	public class biddingRecordRes implements Serializable {  
	    private static final long serialVersionUID = 1L;
	    
	    private int auth;
	    private int price;
	    private int code;
	    
	    public biddingRecordRes() {
	    	super();
	    }
	    
	    public int getAuth() {
	    	return auth;
	    }
	    
	    public void setAuth(int auth) {
	    	this.auth = auth;
	    }
	    
	    public int getPrice() {
	    	return price;
	    }
	    
	    public void setPrice(int price) {
	    	this.price = price;
	    }
	    
	    public int getCode() {
	    	return code;
	    }
	    
	    public void setCode(int code) {
	    	this.code = code;
	    }	    
	}
}