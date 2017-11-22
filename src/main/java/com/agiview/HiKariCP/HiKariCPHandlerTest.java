package com.agiview.HiKariCP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;  
import java.sql.SQLException;  

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.agiview.HiKariCP.HiKariCPHandler;

@Path("/hikaricp")  
public class HiKariCPHandlerTest {  
    @GET  
    @Produces(MediaType.APPLICATION_JSON)  
    public String printTesting() {
    	
    	Connection conn = null;
    	PreparedStatement pst = null; 
    	String sql = null;   
        ResultSet ret = null;  
        
        sql = "select *from userToken";
        
        try {
        	conn = HiKariCPHandler.getConn(); 
            pst = conn.prepareStatement(sql);
            ret = pst.executeQuery(sql);
            while (ret.next()) {  
                String uname = ret.getString(1);  
                String utel = ret.getString(2);  
                String uIDcard = ret.getString(3);  
                String uToken = ret.getString(4);  
                System.out.println(uname);
                System.out.println(utel); 
                System.out.println(uIDcard); 
                System.out.println(uToken); 
            }  
            ret.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return "testing hikaricp";  
    }  
}