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
import com.agiview.control.changeUserStatus;
import com.agiview.member.tokenHandler;

@Path("/biddingPrice")  
public class biddingPrice {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public biddingPriceRes bidding(@Context HttpHeaders hh, biddingPriceParam bp) throws SQLException {
		/*
		Cookie tmp1 = hh.getCookies().get("token");
		Cookie tmp2 = hh.getCookies().get("uuid");
    	String AgiToken = (tmp1 == null) ? null : tmp1.getValue();
    	String uuid = (tmp2 == null) ? null : tmp2.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String uuid = header.getFirst("uuid");
    	biddingPriceRes res = new biddingPriceRes();
    	Connection conn = null;
    	String memberIDcard = null;
    	if(bp == null) {
    		res.setAuth(-1); 
    		res.setBidding(-1);                   // parameter not found
            res.setCode(1000);
            return res;
    	}
    	String auctionID = bp.getAuctionID();
    	int price = bp.getPrice();
    	
    	if (auctionID == null ||  price <= 0) {
    		res.setAuth(-1); 
    		res.setBidding(-1);                   // parameter error
            res.setCode(1000);
            return res;
    	}
    	
    	try {
    		conn = HiKariCPHandler.getConn(); 
    	} catch (SQLException e) {
    		res.setAuth(-1); 
    		res.setBidding(-1);                   // connection failed to get
            res.setCode(2000);
            return res;
    	}
    	
    	if(AgiToken == null && uuid == null) {
    		res.setAuth(-1); 
    		res.setBidding(-1);                   // parameter not found
            res.setCode(1000);
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
    	        		res.setBidding(-1);
    	                res.setCode(1020);    // token expired
    	                return res;
    	    		}
    	    		try {
    	    			int auctionState = changeAuctionState.verifyAuctionState(conn, auctionID);
    	    			if(auctionState == 1) {
        	    			int bidStatus = saveBiddingPrice(conn, auctionID, memberIDcard, price);
        	    			int userStatus = changeUserStatus.updateUserStatus(conn, auctionID, memberIDcard, 3);
    		    			if(bidStatus == 1 && userStatus == 1) {
    			    			res.setAuth(1); 
    			        		res.setBidding(1);
    		    			}
    		    			else {
    		    				res.setAuth(1); 
    			        		res.setBidding(-1);
    			        		res.setCode(1030);       // repeated bidding / userStatus update failed
    		    			}
    	    			}
    	    			else {
    	    				res.setAuth(1);
    	    				res.setBidding(-1);
    	    				res.setCode(1040);           // auction not started OR is over OR not exist
    	    			}
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	        		res.setBidding(-1);
    	                res.setCode(2000);          // saving biddingPrice failed
    	                conn.close();
    	                return res;
    	    		}
    	    	}
    	        else {
    	        	res.setAuth(-1); 
            		res.setBidding(-1);
                    res.setCode(1020);            // token not match
    	        }
    	        ret.close();
    	        pst.close();
    			conn.close();
                return res;
    		} catch (SQLException e) {
    			e.printStackTrace();
    			res.setAuth(-1); 
        		res.setBidding(-1);
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
    	        	int auctionState = changeAuctionState.verifyAuctionState(conn, auctionID);
    	        	if(auctionState == 1) {    
        	        	int bidStatus = saveBiddingPrice(conn, auctionID, IDcard, price);
        	        	int userStatus = changeUserStatus.updateUserStatus(conn, auctionID, IDcard, 3);   // skipping for payment
	    	        	if(bidStatus == 1 && userStatus == 1) {
	    	    			res.setAuth(1); 
	    	        		res.setBidding(1);
	        			}
	        			else {
	        				res.setAuth(1); 
	    	        		res.setBidding(-1);
	    	        		res.setCode(1030);        // repeated bidding / userStatus update failed
	        			}
    	        	}
    	        	else {
    	        		res.setAuth(1);
    	        		res.setBidding(-1);
    	        		res.setCode(1040);            // auction not started OR is over
    	        	}
    	        }
    	        else {
    	        	res.setAuth(-1); 
            		res.setBidding(-1);               // uuid not match
                    res.setCode(1020);
    	        }
        		ret.close();
        		pst.close();
        		conn.close();
                return res;
    	    	
    		} catch (SQLException e){
    			e.printStackTrace();
    			res.setAuth(-1); 
        		res.setBidding(-1);
                res.setCode(2000);                // uuid verify failed
                conn.close();
                return res;
    		}
    	}
    	else {
    		res.setAuth(-1); 
    		res.setBidding(-1);
            res.setCode(1000);                    // parameter redundant
            conn.close();
            return res;
    	}
    	
	}
	
	public int saveBiddingPrice(Connection conn, String auctionID, String identity, int price) throws SQLException {
    	PreparedStatement pst = null;
    	ResultSet ret = null;
		String sql1 = "SELECT biddingPrice FROM biddingPrice WHERE auctionID=? and identity=?;";
		
        pst = conn.prepareStatement(sql1);
        pst.setString(1, auctionID);
        pst.setString(2, identity);
        ret = pst.executeQuery();
        if(ret.next()) {
        	return 0;          // biddingPrice record existed, repeated bidding
        }
        else {
        	ret.close();
    		pst.close();
    		java.util.Date date = new java.util.Date();
            Timestamp timeStamp = new Timestamp(date.getTime());
    		String sql2 = "INSERT INTO biddingPrice (auctionID, identity, biddingPrice, paymentState, heat, timeStamp) VALUES (?,?,?,?,?,?);";
    		
    		pst = conn.prepareStatement(sql2);
    		pst.setString(1, auctionID);
            pst.setString(2, identity);
            pst.setInt(3, price);
            //pst.setInt(4, 0);
            pst.setInt(4, 1);                            // used for testing, bypassing payment & payment confirm
            pst.setInt(5, 0);
            pst.setTimestamp(6,timeStamp);
            pst.executeUpdate();
            return 1;
        }
	}
	
	public class biddingPriceRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	    
	    private int auth;
	    private int bidding;
	    private int code;
	    
	    public biddingPriceRes() {
	    	super();
	    }
	    
	    public int getAuth() {
	    	return auth;
	    }
	    
	    public void setAuth(int auth) {
	    	this.auth = auth;
	    }
	    
	    public int getBidding() {
	    	return bidding;
	    }
	    
	    public void setBidding(int bidding) {
	    	this.bidding = bidding;
	    }
	    
	    public int getCode() {
	    	return code;
	    }
	    
	    public void setCode(int code) {
	    	this.code = code;
	    }
	}
}

