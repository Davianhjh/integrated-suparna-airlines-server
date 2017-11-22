package com.agiview.visitor;

import java.util.UUID;
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

@Path("/visitorIndex")  
public class visitorIndex {
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public visitorIndexRes index(visitorIndexParam vs) {
    	visitorIndexRes res = new visitorIndexRes();
    	String generateID = null;
		if(vs == null) {
    		res.setAuth(-1); 
            res.setCode(1000);
            return res;
		}
    	String username = vs.getUsername();
    	String tel = vs.getTel();
    	String IDcard = vs.getIDcard();
    	
    	if(username == null || tel == null || IDcard == null || username == "" || tel == "" || IDcard == "") {
    		res.setAuth(-1); 
            res.setCode(1000);
            return res;
    	}
    	try {
    		generateID = registeVisitor(username, tel, IDcard);
    		if(generateID != null) {
    			res.setAuth(1);
    			res.setUUID(generateID);
    		}
    		else {
    			res.setAuth(-1);
				res.setCode(2000);
    		}
    	} catch (SQLException e){
    		e.printStackTrace();
			res.setAuth(-1);
			res.setCode(2000);
    	}
    	return res;
	}
	
	public String registeVisitor(String username, String tel, String IDcard) throws SQLException {
		Connection conn = null;
    	PreparedStatement pst = null;   
        ResultSet ret = null;
        String uuid = null;
        String sql1 = "SELECT tel, uuid FROM userToken WHERE username=? and IDcard=? and type=?;";
        conn = HiKariCPHandler.getConn(); 
        pst = conn.prepareStatement(sql1);
    	pst.setString(1, username);
    	pst.setString(2, IDcard);
    	pst.setInt(3, 2);
    	ret = pst.executeQuery();
    	if(ret.next()) {
    		uuid = ret.getString(2);
    		if(tel != ret.getString(1)) {
    			String sql2 = "UPDATE userToken SET tel=? where uuid=?;";
    			pst = conn.prepareStatement(sql2);
    			pst.setString(1, tel);
    			pst.setString(2, uuid);
    			pst.executeUpdate();
    		}
    	}
    	else {
    		pst.close();
    		uuid = UUID.randomUUID().toString();
    		String sql3 = "INSERT INTO userToken (username, tel, IDcard, uuid, type) VALUES (?,?,?,?,?);";
    		pst = conn.prepareStatement(sql3);
			pst.setString(1, username);
			pst.setString(2, tel);
			pst.setString(3, IDcard);
			pst.setString(4, uuid);
			pst.setInt(5, 2);
			pst.executeUpdate();
    	}
    	ret.close();
    	pst.close();
    	conn.close();
    	return uuid;
	}
	
	public class visitorIndexRes implements Serializable {  
	    private static final long serialVersionUID = 1L;  
	  
	    private int auth;  
	    private String uuid;
	    private int code;
	    
	    public visitorIndexRes() {
	    	super();
	    }
	    
	    public int getAuth() {
	    	return auth;
	    }
	    
		public void setAuth(int i) {
			this.auth = i;
		}  
		
		public String getUUID() {
			return uuid;
		}
		
		public void setUUID(String uuid) {
			this.uuid = uuid;
		}
		
		public int getCode() {
			return code;
		}
		
		public void setCode(int code) {
			this.code = code;
		}
	}
}
