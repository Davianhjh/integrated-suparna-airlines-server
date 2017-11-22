package com.agiview.auction;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

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

@Path("/passengerFlight") 
public class passengerFlight {
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public passengerFlightRes flights(@Context HttpHeaders hh, passengerFlightParam pf) throws Exception {
		/*
		Cookie tmp1 = hh.getCookies().get("token");
		Cookie tmp2 = hh.getCookies().get("uuid");
    	String AgiToken = (tmp1 == null) ? null : tmp1.getValue();
    	String uuid = (tmp2 == null) ? null : tmp2.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String uuid = header.getFirst("uuid");
    	passengerFlightRes res = new passengerFlightRes();
    	Connection conn = null;
    	String auctionID = null;
    	String IDcard = null;
    	String username = null;
    	if(pf != null) {
    		auctionID = pf.getAuctionID();
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
    			String sql = "SELECT username from userToken where token=?;";
    	    	PreparedStatement pst = null;
    	    	ResultSet ret= null;
    	        pst = conn.prepareStatement(sql);
    	        pst.setString(1, AgiToken);
    	        ret = pst.executeQuery();
    	        if(ret.next()) { 
    	    		try {
    	    			IDcard = tokenHandler.parseJWT(AgiToken);
    	    			username = ret.getString(1);
    	    			res.setName(username);
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    			res.setAuth(-1); 
    	                res.setCode(1020);                // token expired
    	    		}
    	    		try {
    		        	if(auctionID == null)
    		        		passengerTicket.getTicket(conn, IDcard, res);
    		        	else passengerTicket.getOneTicket(conn, IDcard, auctionID, res);
    	    			res.setAuth(1); 
    	    		} catch (SQLException | ParseException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	                res.setCode(2000);                // getting passenger tickets failed
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
    			String sql = "SELECT username, IDcard from userToken where uuid=?;";
    	    	PreparedStatement pst = null;
    	    	ResultSet ret= null;
    	        pst = conn.prepareStatement(sql);
    	        pst.setString(1, uuid);
    	        ret = pst.executeQuery();
    	        if(ret.next()) {
	    			username = ret.getString(1);
    	        	IDcard = ret.getString(2);
	    			res.setName(username);
    	        	try {
	    	        	if(auctionID == null)
	    	        		passengerTicket.getTicket(conn, IDcard, res);
	    	        	else passengerTicket.getOneTicket(conn, IDcard, auctionID, res);
	        			res.setAuth(1); 
    	        	} catch (SQLException | ParseException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	                res.setCode(2000);                // getting passenger tickets failed
    	                conn.close();
    	                return res;
    	        	}
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
	
	public class passengerFlightRes implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private int auth;
		private int code;
		private String name;
		private ArrayList<flightInfo> flights;
		
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
		
		public void setName(String username) {
			this.name = username;
		}
		
		public String getName() {
			return name;
		}
		
		public ArrayList<flightInfo> getFlights(){
			return flights;
		}
		
		public void setFlights(ArrayList<flightInfo> fi) {
			this.flights = new ArrayList<flightInfo>(fi);
		}
	}
}
