package com.agiview.control;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

@Path("/flightManage")  
public class flightManage {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public flightManageRes manage(@Context HttpHeaders hh, flightManageParam fm) throws SQLException {
		/*
		Cookie tmp = hh.getCookies().get("token");
    	String AgiToken = (tmp == null) ? null : tmp.getValue();
    	*/
    	int dayLap;
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	flightManageRes res = new flightManageRes();
    	Connection conn = null;
    	
    	try {
    		dayLap = fm.getDayLap();
    	} catch (RuntimeException e) {
    		dayLap = 3;
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
    	    			flightList.getList(conn, dayLap, res);
    	    			res.setAuth(1);
    	    		} catch (SQLException e) {
    	    			e.printStackTrace();
    	    			res.setAuth(1);
    	    			res.setCode(2000);
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
	
	public class flightManageRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
		
	    private int auth;
	    private int code;
	    private ArrayList<flight> flights;
	    
	    public flightManageRes() {
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
		
		public ArrayList<flight> getFlights(){
			return flights;
		}
		
		public void setFlights(ArrayList<flight> flights) {
			this.flights = new ArrayList<flight>(flights);
		}
	}
}
