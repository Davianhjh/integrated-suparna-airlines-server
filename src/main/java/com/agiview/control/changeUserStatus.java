package com.agiview.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class changeUserStatus {

	public changeUserStatus() {
		super();
	}
	
	public static int updateUserStatus(Connection conn, String auctionID, String identity, int userstatus) {
		try {			
	    	PreparedStatement pst = null;
			String sql = "UPDATE userStates set userStatus=?, timeStamp=? WHERE auctionID=? and identity=?;";
			
			java.util.Date date = new java.util.Date();
	        Timestamp timeStamp = new Timestamp(date.getTime());
	        pst = conn.prepareStatement(sql);
	        pst.setInt(1, userstatus);
	        pst.setTimestamp(2, timeStamp);
	        pst.setString(3, auctionID);
	        pst.setString(4, identity);
	        pst.executeUpdate();		
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
