package com.agiview.mysqlHelper;
import java.sql.ResultSet;  
import java.sql.SQLException;  

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/mysql")  
public class mysqlHelperTest {  
    @GET  
    @Produces(MediaType.APPLICATION_JSON)  
    public String printTesting() {  
    	String sql = null;  
        mysqlHelper db1 = null;  
        ResultSet ret = null;  
        
        sql = "select *from userToken";
        db1 = new mysqlHelper(sql);  
  
        try {  
            ret = db1.pst.executeQuery();  
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
            db1.close();
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return "testing mysqlHelper";  
    }  
}
