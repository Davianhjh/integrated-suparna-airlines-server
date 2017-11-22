package com.agiview.member;

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

@Path("/memberIndex")  
public class memberIndex {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public memberIndexRes index(@Context HttpHeaders hh, memberIndexParam mm) {
		/*
		Cookie tmp1 = hh.getCookies().get("token");
    	String serverToken = (tmp1 == null) ? null : tmp1.getValue();
    	*/
    	MultivaluedMap<String, String> header = hh.getRequestHeaders();
    	String AgiToken = header.getFirst("token");
    	String serverToken = header.getFirst("loginToken");
    	memberIndexRes res = new memberIndexRes();
    	String token = null;
    	if(mm == null) {
    		res.setAuth(-1);
    		res.setCode(1000);
    		return res;
    	}
		String action = mm.getAction();
    	
		switch(action) {
			case "signin":
				try {
			    	if(serverToken != null) {
			    		token = registeMember(serverToken);  
						if(token != null) {
							res.setAuth(1); 
							res.setToken(token);
						}
						else {
							res.setAuth(-1);
				    		res.setCode(1010);
						}
			    	}
			    	else {
			    		res.setAuth(-1);
			    		res.setCode(1000);
			    	}
				} catch (SQLException e) {
					e.printStackTrace();
					res.setAuth(-1);
					res.setCode(2000);
				}
		    	break;
			case "resume":
				try {
					if(AgiToken != null) {						
						boolean result = verifyAgiToken(AgiToken);
						if(result) {
							res.setAuth(1); 
							res.setToken(AgiToken);
						}
						else {
							res.setAuth(-1);
				    		res.setCode(1020);
						}
					}
					else {
			    		res.setAuth(-1);
			    		res.setCode(1000);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					res.setAuth(-1);
					res.setCode(2000);
				}
				break;
			default:
				res.setAuth(-1);
				res.setCode(1000);
				break;
		}
		return res;
    }
	
	public String registeMember(String serverToken) throws SQLException {
		Connection conn = null;
    	PreparedStatement pst = null;   
        ResultSet ret1,ret2 = null;   
        String token = null;
        String sql1 = "SELECT username, tel, IDcard FROM memberShip WHERE token=?;";
        
    	// TODO 
    	// get member information from Suparna Airlines Membership System
    	conn = HiKariCPHandler.getConn(); 
        pst = conn.prepareStatement(sql1);
        pst.setString(1, serverToken);
        ret1 = pst.executeQuery();
        while (ret1.next()) {
            String username = ret1.getString(1);  
            String tel = ret1.getString(2);
            String IDcard = ret1.getString(3);
            token = tokenHandler.createJWT(username, tel, IDcard, 24*3600*1000);
            String sql2 = "SELECT IDcard FROM userToken WHERE username=? and IDcard=?;";
            pst = conn.prepareStatement(sql2);
            pst.setString(1, username);
            pst.setString(2, IDcard);
            ret2 = pst.executeQuery();
            if (ret2.next()) {
            	String sql3 = "UPDATE userToken SET token=? WHERE IDcard=? and type=?;";
            	pst = conn.prepareStatement(sql3);
 	            pst.setString(1, token);
 	            pst.setString(2, IDcard);
 	            pst.setInt(3, 1);
 	            pst.executeUpdate();
 	            ret2.close();	
 	            pst.close();
            }
            else {
            	ret2.close();
            	pst.close();
	            String sql4 = "INSERT INTO userToken (username, tel, IDcard, token, type) VALUES (?,?,?,?,?);";
	            pst = conn.prepareStatement(sql4);
	            pst.setString(1, username);
	            pst.setString(2, tel);
	            pst.setString(3, IDcard);
	            pst.setString(4, token);
	            pst.setInt(5, 1);
	            pst.executeUpdate();
            }
        }
        ret1.close();
        conn.close();
        return token;
	}
	
	public boolean verifyAgiToken(String AgiToken) throws SQLException {
		Connection conn = null;
    	PreparedStatement pst = null;   
        ResultSet ret = null;   
        String sql = "SELECT username, IDcard FROM userToken WHERE token=?;";
        conn = HiKariCPHandler.getConn(); 
        pst = conn.prepareStatement(sql);
        pst.setString(1, AgiToken);
        ret = pst.executeQuery();
        if(ret.next()) {
        	try {
        		tokenHandler.parseJWT(AgiToken);
        		ret.close();
        		pst.close();
        		conn.close();
        		return true;
        	} catch (Exception e) {
        		e.printStackTrace();
        		ret.close();
        		pst.close();
        		conn.close();
        		return false;
        	}
        }
        else {
        	ret.close();
        	pst.close();
        	conn.close();
        	return false;
        }
	}
	
	public class memberIndexRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	  
	    private int auth;  
	    private String agiToken;
	    private int code;
	    
	    public memberIndexRes() {
	    	super();
	    }
	    
	    public int getAuth() {
	    	return auth;
	    }
	    
		public void setAuth(int i) {
			this.auth = i;
		}  
		
		public String getToken() {
			return agiToken;
		}
		
		public void setToken(String token) {
			this.agiToken = token;
		}
		
		public int getCode() {
			return code;
		}
		
		public void setCode(int code) {
			this.code = code;
		}
	}
}