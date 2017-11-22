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

@Path("/checkResult")  
public class CheckResult {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public checkResultRes check(@Context HttpHeaders hh, timeRemainParam tr) throws Exception {
		/*
		Cookie tmp1 = hh.getCookies().get("token");
		Cookie tmp2 = hh.getCookies().get("uuid");
    	String AgiToken = (tmp1 == null) ? null : tmp1.getValue();
    	String uuid = (tmp2 == null) ? null : tmp2.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String uuid = header.getFirst("uuid");
    	checkResultRes res = new checkResultRes();
    	Connection conn = null;
    	String IDcard = null;
    	if(tr == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                   // parameter not found
            return res;
    	}
    	String auctionID = tr.getAuctionID();
    	
    	if(auctionID == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                   // parameter not found
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
    	    	ResultSet ret = null;
    	        pst = conn.prepareStatement(sql);
    	        pst.setString(1, AgiToken);
    	        ret = pst.executeQuery();
    	        if(ret.next()) { 
    	    		try {
    	    			IDcard = tokenHandler.parseJWT(AgiToken);
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    			res.setAuth(-1); 
    	                res.setCode(1020);                // token expired
    	                conn.close();
    	                return res;
    	    		}
    	    		try {
    	    			pst.close();
    		        	String sql1 = "SELECT biddingPrice FROM auctionResult WHERE auctionID=? AND identity=?;";
    		        	
    		        	pst = conn.prepareStatement(sql1);
    		        	pst.setString(1, auctionID);
    		        	pst.setString(2, IDcard);
    		        	ret = pst.executeQuery();
    		        	if(ret.next()) {
    		        		res.setAuth(1);
    		        		res.setHit(1);
    		        		res.setPrice(ret.getInt(1));              // hit
    		        	}
    		        	else {
    		        		ret.close();
    		        		pst.close();
    		        		String sql2 = "SELECT biddingPrice FROM biddingPrice WHERE auctionID=? AND identity=? AND paymentState=?;";
    		        		
    		        		pst = conn.prepareStatement(sql2);
    		            	pst.setString(1, auctionID);
    		            	pst.setString(2, IDcard);
    		            	pst.setInt(3, 1);
    		            	ret = pst.executeQuery();
    		            	if(ret.next()) {
    		            		res.setAuth(1);
    		            		res.setHit(0);
    		            		res.setPrice(ret.getInt(1));           // miss-hit
    		            	}
    		            	else {
    		            		res.setAuth(1);
    		            		res.setHit(0);
    		            		res.setCode(1040);                     // not paid OR not bid
    		            	}
    		        	}
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	        		res.setHit(0);
    	                res.setCode(2000);                             // sql server error
    	                conn.close();
    	                return res;
    	    		}
    	        }
    	        else {
    	        	res.setAuth(-1); 
    	        	res.setHit(0);
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
    			String sql3 = "SELECT IDcard from userToken where uuid=?;";
    	    	PreparedStatement pst = null;
    	    	ResultSet ret= null;
    	        pst = conn.prepareStatement(sql3);
    	        pst.setString(1, uuid);
    	        ret = pst.executeQuery();
    	        if(ret.next()) {
    	        	IDcard = ret.getString(1);
    	        	String sql4 = "SELECT biddingPrice FROM auctionResult WHERE auctionID=? AND identity=?;";
    	        	
    	        	pst = conn.prepareStatement(sql4);
    	        	pst.setString(1, auctionID);
    	        	pst.setString(2, IDcard);
    	        	ret = pst.executeQuery();
    	        	if(ret.next()) {
    	        		res.setAuth(1);
    	        		res.setHit(1);
    	        		res.setPrice(ret.getInt(1));              // hit
    	        	}
    	        	else {
    	        		ret.close();
    	        		pst.close();
    	        		String sql5 = "SELECT biddingPrice FROM biddingPrice WHERE auctionID=? AND identity=? AND paymentState=?;";
    	        		
    	        		pst = conn.prepareStatement(sql5);
    	            	pst.setString(1, auctionID);
    	            	pst.setString(2, IDcard);
    	            	pst.setInt(3, 1);
    	            	ret = pst.executeQuery();
    	            	if(ret.next()) {
    	            		res.setAuth(1);
    	            		res.setHit(0);
    	            		res.setPrice(ret.getInt(1));           // miss-hit
    	            	}
    	            	else {
    	            		res.setAuth(1);
    	            		res.setHit(0);
    	            		res.setCode(1040);                     // not paid OR not bid
    	            	}
    	        	}   	        	
    	        }
    	        else {
    	        	res.setAuth(-1); 
            		res.setHit(0);               // uuid not match
                    res.setCode(1020);
    	        }
    	        ret.close();
    	        pst.close();
	    		conn.close();
	    		return res; 
    	        
    		} catch (SQLException e) {
    			e.printStackTrace();
    			res.setAuth(1); 
        		res.setHit(0);
                res.setCode(2000);                             // sql server error
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
	
	public class checkResultRes implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private int auth;
		private int code;
		private int hit;
		private int price;
		
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
		
		public int getHit() {
			return hit;
		}
		
		public void setHit(int hit) {
			this.hit = hit;
		}
		
		public int getPrice() {
			return price;
		}
		
		public void setPrice(int price) {
			this.price = price;
		}
	}
}
