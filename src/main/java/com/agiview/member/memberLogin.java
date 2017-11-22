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
import javax.ws.rs.core.MediaType;

import com.agiview.HiKariCP.HiKariCPHandler;

@Path("/memberLogin")  
public class memberLogin {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public memberLoginRes login(memberLoginParam mm) {
		memberLoginRes res = new memberLoginRes();    	
		if(mm == null) {
    		res.setAuth(-1); 
            res.setCode(1000);
            return res;
		}
    	String memberID = mm.getMemberID();
    	String password = mm.getPassword();
    	
    	if(memberID == null || password == null) {
    		res.setAuth(-1); 
            res.setCode(1000);
            return res;
    	}
    	Connection conn = null;
    	PreparedStatement pst = null;   
        ResultSet ret = null;  
    	String token = null;
        String sql1 = "SELECT username, IDcard FROM memberShip WHERE memberID=? and password=?;";
        try {
        	conn = HiKariCPHandler.getConn(); 
            pst = conn.prepareStatement(sql1);
            pst.setString(1, memberID);
            pst.setString(2, password);
            ret = pst.executeQuery();
            if(ret.next()) {
            	String username = ret.getString(1);  
	            String IDcard = ret.getString(2);
	            token = tokenHandler.createJWT(memberID, username, IDcard, 24*3600*1000);
	            String sql2 = "UPDATE memberShip SET token=? WHERE memberID=?;";
	            pst = conn.prepareStatement(sql2);
	            pst.setString(1, token);
	            pst.setString(2, memberID);
	            pst.executeUpdate();
	            ret.close();
	            pst.close();
	            conn.close();
	            res.setAuth(1); 
		        res.setToken(token);
		        return res;
            }
            else {
            	ret.close();
            	pst.close();
            	conn.close();
	            res.setAuth(-1); 
	            res.setCode(1010);
	            return res;
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            res.setAuth(-1); 
	        res.setCode(1010);
            return res;
        }
	}
	
	public class memberLoginRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	  
	    private int auth;
	    private int code;
	    private String token;
	    
	    public memberLoginRes() {
	    	super();
	    }
	    
	    public int getAuth() {
	    	return auth;
	    }
	    
		public void setAuth(int i) {
			this.auth = i;
		}  
		
		public String getToken() {
			return token;
		}
		
		public void setToken(String token) {
			this.token = token;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public void setCode(int code) {
			this.code = code;
		}
	}
}
