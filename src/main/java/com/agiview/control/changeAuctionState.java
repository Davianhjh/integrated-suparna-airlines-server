package com.agiview.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class changeAuctionState {
	
	public changeAuctionState () {
		super();
	}
	
	public static int updateAuctionState(Connection conn, String auctionID, int state) {
		try {			
	    	PreparedStatement pst = null;
			String sql = "UPDATE auctionParam set auctionState=? WHERE auctionID=?;";
			
	        pst = conn.prepareStatement(sql);
	        pst.setInt(1, state);
	        pst.setString(2, auctionID);
	        pst.executeUpdate();
	        //pst.close();
	        //conn.close();
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static int verifyAuctionState(Connection conn, String auctionID) {
		int result = 0;
    	PreparedStatement pst = null;
    	ResultSet ret = null;
		String sql = "SELECT auctionState FROM auctionParam WHERE auctionID=?;";
		
		try {			
	        pst = conn.prepareStatement(sql);
	        pst.setString(1, auctionID);
	        ret = pst.executeQuery();	
	        if(ret.next()) {
	        	if(ret.getInt(1) == 1)
	        		result = 1;
	        }
	        //ret.close();
	        //pst.close();
	        //conn.close();
	        return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static void updateFlightState(Connection conn, String auctionID) throws SQLException {	
    	PreparedStatement pst = null;
    	ResultSet ret = null;
		String sql1 = "SELECT auctionManage.flightNo, date, state FROM auctionManage INNER JOIN flightManage ON auctionManage.flightNo="
				+ "flightManage.flightNo AND auctionManage.date=flightManage.flightDate WHERE auctionManage.auctionID=?;";
		
        pst = conn.prepareStatement(sql1);
        pst.setString(1, auctionID);
        ret = pst.executeQuery();
        if(ret.next()) {
        	String flightNo = ret.getString(1);
        	String flightDate = ret.getString(2);
        	int state = ret.getInt(3);
        	pst.close();
        	
        	String sql3 = "UPDATE flightManage set state=? WHERE flightNo=? AND flightDate=?;";
        	pst = conn.prepareStatement(sql3);
        	pst.setInt(1, state-1);
        	pst.setString(2, flightNo);
        	pst.setString(3, flightDate);
        	pst.executeUpdate();
        }
	}
}
