package com.agiview.mysqlHelper;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.SQLException; 
  
public class mysqlHelper {  
    public static final String url = "jdbc:mysql://127.0.0.1/test";  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "114432";  
  
    public Connection conn = null;  
    public PreparedStatement pst = null; 
  
    public mysqlHelper(String sql) {  
        try {  
            Class.forName(name); 
            conn = DriverManager.getConnection(url, user, password); 
            pst = conn.prepareStatement(sql); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public void close() {  
        try {  
            this.conn.close();  
            this.pst.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
}  
