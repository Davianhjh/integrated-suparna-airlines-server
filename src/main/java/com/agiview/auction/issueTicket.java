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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.agiview.HiKariCP.HiKariCPHandler;
import com.agiview.member.tokenHandler;

@Path("/issueTicket")
public class issueTicket{
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public issueTicketRes issue(@Context HttpHeaders hh, issueTicketParam it) throws Exception {
		/*
		Cookie tmp1 = hh.getCookies().get("token");
		Cookie tmp2 = hh.getCookies().get("uuid");
    	String AgiToken = (tmp1 == null) ? null : tmp1.getValue();
    	String uuid = (tmp2 == null) ? null : tmp2.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String uuid = header.getFirst("uuid");
    	issueTicketRes res = new issueTicketRes();
    	Connection conn = null;
    	String IDcard = null;
    	String username = null;
    	if(it == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                   // parameter not found
            return res;
    	}
    	String auctionID = it.getAuctionID();
    	
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
    			String sql = "SELECT username from userToken where token=?;";
    	    	PreparedStatement pst = null;
    	    	ResultSet ret, ret2 = null;
    	        pst = conn.prepareStatement(sql);
    	        pst.setString(1, AgiToken);
    	        ret = pst.executeQuery();
    	        if(ret.next()) { 
    	    		try {
    	    			username = ret.getString(1);
    	    			IDcard = tokenHandler.parseJWT(AgiToken);
    	    			res.setName(username);
    	    		} catch (Exception e) {
    	    			e.printStackTrace();
    	    			res.setAuth(-1); 
    	                res.setCode(1020);                // token expired
    	                conn.close();
    	                return res;
    	    		}
    	    		try {
    	    			res.setAuth(1);
    	    			pst.close();
    		        	String sql1 = "SELECT biddingPrice FROM auctionResult WHERE auctionID=? AND identity=?;";
    		        	
    		        	pst = conn.prepareStatement(sql1);
    		        	pst.setString(1, auctionID);
    		        	pst.setString(2, IDcard);
    		        	ret2 = pst.executeQuery();
    		        	if(ret2.next()) {
    		        		getNewTicket(conn, IDcard, auctionID, res);
    		        	}
    		        	else {
    		        		res.setCode(1040);             // user not won the auction
    		        	}
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1); 
    	                res.setCode(2000);                             // sql server error
    	                conn.close();
    	                return res;
    	    		}
    	        }
    	        else {
    	        	res.setAuth(-1);                     // token not match
                    res.setCode(1020);
    	        }
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
    			String sql3 = "SELECT username, IDcard from userToken where uuid=?;";
    	    	PreparedStatement pst = null;
    	    	ResultSet ret= null;
    	        pst = conn.prepareStatement(sql3);
    	        pst.setString(1, uuid);
    	        ret = pst.executeQuery();
    	        if(ret.next()) {
    	        	username = ret.getString(1);
    	        	IDcard = ret.getString(2);
    	        	res.setAuth(1);
    	        	res.setName(username);
    	            String sql4 = "SELECT biddingPrice FROM auctionResult WHERE auctionID=? AND identity=?;";
    	        	
    	        	pst = conn.prepareStatement(sql4);
    	        	pst.setString(1, auctionID);
    	        	pst.setString(2, IDcard);
    	        	ret = pst.executeQuery();
    	        	if(ret.next()) {
    	        		getNewTicket(conn, IDcard, auctionID, res);
    	        	}
    	        	else {
		        		res.setCode(1040);             // user not won the auction
    	        	}
    	        }
    	        else {
    	        	res.setAuth(-1); 
                    res.setCode(1020);                         // uuid not match
    	        }
	        	conn.close();
	        	return res;
    		} catch (SQLException e) {
    			e.printStackTrace();
    			res.setAuth(1);
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
	
	public void getNewTicket(Connection conn, String identity, String auctionID, issueTicketRes res) throws SQLException {
    	PreparedStatement pst = null;
    	ResultSet ret= null;
    	String sql = "SELECT flightNo, date, orgCity, dstCity FROM auctionManage WHERE auctionID=?;";
    	
    	pst = conn.prepareStatement(sql);
    	pst.setString(1, auctionID);
    	ret = pst.executeQuery();
    	if(ret.next()) {
    		String flightNo = ret.getString(1);
    		String date = ret.getString(2);
    		res.setFlightNo(flightNo);
    		res.setDate(date);
    		res.setOrgCity(ret.getString(3));
    		res.setDstCity(ret.getString(4));
    		res.setCarbin("ÉÌÎñ²Õ");
    		getNewTicketInfo(identity, flightNo, date, res);
    	}
	}
	
	public void getNewTicketInfo(String identity, String flightNo, String date, issueTicketRes res) {
		// TODO
		// refund a ticket and issue a new one
		// get new ticketNo & seatNo
		String ticketNo = "TNO404";
		String seatNo = "A11";
		res.setTicketNo(ticketNo);
		res.setSeatNo(seatNo);
	}
	
	public class issueTicketRes implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private int auth;
		private int code;
		private String name;
		private String flightNo;
		private String date;
		private String orgCity;
		private String dstCity;
		private String carbin;
		private String seatNo;
		private String ticketNo;
		private String entrance;
		private String onBoard;
		
		public issueTicketRes() {
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
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
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
		
		public void setDate(String date) {
			this.date = date;
		}
		
		public String getOrgCity() {
			return orgCity;
		}
		
		public void setOrgCity(String orgCity) {
			this.orgCity = orgCity;
		}
		
		public String getDstCity() {
			return dstCity;
		}
		
		public void setDstCity(String dstCity) {
			this.dstCity = dstCity;
		}
		
		public String getCarbin() {
			return carbin;
		}
		
		public void setCarbin(String carbin) {
			this.carbin = carbin;
		}
		
		public String getSeatNo() {
			return seatNo;
		}
		
		public void setSeatNo(String seatNo) {
			this.seatNo = seatNo;
		}
		
		public String getTicketNo() {
			return ticketNo;
		}
		
		public void setTicketNo(String ticketNo) {
			this.ticketNo = ticketNo;
		}
		
		public String getEntrance() {
			return entrance;
		}
		
		public void setEntrance(String entrance) {
			this.entrance = entrance;
		}
		
		public String getOnBoard() {
			return onBoard;
		}
		
		public void setOnBoard(String onBoard) {
			this.onBoard = onBoard;
		}
	}
}
