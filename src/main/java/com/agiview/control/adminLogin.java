package com.agiview.control;

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
import com.agiview.member.tokenHandler;

@Path("/adminLogin")  
public class adminLogin {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public adminLoginRes login(adminLoginParam al) {
		adminLoginRes res = new adminLoginRes();
		if(al == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                      // parameter not found
            return res;
		}
    	String admin = al.getAdmin();
    	String password = al.getPassword();
    	
    	if(admin == null || password == null) {
    		res.setAuth(-1); 
            res.setCode(1000);                      // parameter not found
            return res;
    	}
    	Connection conn = null;
    	PreparedStatement pst = null;   
        ResultSet ret = null;  
    	String token = null;
        String sql1 = "SELECT name, tel FROM adminToken WHERE admin=? and password=?;";
        try {
        	conn = HiKariCPHandler.getConn(); 
            pst = conn.prepareStatement(sql1);
            pst.setString(1, admin);
            pst.setString(2, password);
            ret = pst.executeQuery();
            if(ret.next()) {
            	String name = ret.getString(1);  
            	String tel = ret.getString(2);
	            token = tokenHandler.createJWT(name, tel, admin, 24*3600*1000);
	            String sql2 = "UPDATE adminToken SET token=? WHERE admin=?;";
	            pst = conn.prepareStatement(sql2);
	            pst.setString(1, token);
	            pst.setString(2, admin);
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
	            res.setCode(1010);             // admin & password not match
	            return res;
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            res.setAuth(-1); 
	        res.setCode(2000);                 // verify admin & password failed
            return res;
        }
	}
	
	public class adminLoginRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	  
	    private int auth;
	    private int code;
	    private String token;
	    
	    public adminLoginRes() {
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
