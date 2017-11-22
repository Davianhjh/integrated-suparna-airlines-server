package com.agiview.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class endAuction {
	
	private String[] winners;
	private int reservedSeats;
	private ArrayList<String> losers;
	
	public endAuction() {
		// TODO
		// get the number of the reserved seats (reserveSeats)
		this.reservedSeats = 1;
		this.winners = new String[reservedSeats];
		this.losers = new ArrayList<String>();
	}
	
	public void getAuctionResult (Connection conn, String auctionID) throws SQLException {
		int basePrice = 0;
		int count = 0;
		String auctionType = null;
    	PreparedStatement pst = null;
    	ResultSet ret1, ret2= null;
    	String sql1 = "SELECT auctionType, basePrice FROM auctionParam WHERE auctionID=?;";
    	
    	pst = conn.prepareStatement(sql1);
    	pst.setString(1, auctionID);
    	ret1 = pst.executeQuery();
    	if(ret1.next()) {
    		auctionType = ret1.getString(1);
    		basePrice = ret1.getInt(2);
    		String sql2 = "UPDATE auctionParam set seatNo=?, auctionState=? WHERE auctionID=?;";
    		pst = conn.prepareStatement(sql2);
        	pst.setInt(1, reservedSeats);
        	pst.setInt(2, 2);
        	pst.setString(3, auctionID);
        	pst.executeUpdate();
    		
    		ret1.close();
    		pst.close();
        	String sql3 = "SELECT identity, biddingPrice, timeStamp FROM biddingPrice WHERE auctionID=? AND paymentState=? "
        			+ "AND biddingPrice>? ORDER BY biddingPrice DESC;";
        	pst = conn.prepareStatement(sql3);
        	pst.setString(1, auctionID);
        	pst.setInt(2, 1);
        	pst.setInt(3, basePrice);
        	ret2 = pst.executeQuery();
        	while (ret2.next()) {
        		if(count < reservedSeats) {
	        		String identity = ret2.getString(1);
	        		int biddingPrice = ret2.getInt(2);
	        		Timestamp time = ret2.getTimestamp(3);
	        		winners[count++] = identity;
	        		
	        		String sql4 = "INSERT INTO auctionResult (auctionID, identity, biddingPrice, timeStamp) VALUES (?,?,?,?);";
	        		pst = conn.prepareStatement(sql4);
	            	pst.setString(1, auctionID);
	            	pst.setString(2, identity);
	            	pst.setInt(3, biddingPrice);
	            	pst.setTimestamp(4, time);
	            	pst.executeUpdate();
        		}
        		else {
        			String identity = ret2.getString(1);
        			losers.add(identity);
        		}
        	}
    	}
    	else {
    		throw new SQLException();
    	}
    }
	
	public void sendNotification(Connection conn, String auctionID) throws SQLException {
		// TODO
		// send WeiChat notifications for both winners and losers;
		System.out.println("WeiChat notifications have been set");
	}
	
	public void sendMsg(Connection conn, String auctionID) throws SQLException {
    	PreparedStatement pst = null;
    	ResultSet ret1, ret2= null;
    	String sql1 = "SELECT flightNo, MsgSwitch FROM auctionParam WHERE auctionID=?;";
    	 
    	pst = conn.prepareStatement(sql1);
    	pst.setString(1, auctionID);
    	ret1 = pst.executeQuery();
    	if(ret1.next()) {
    		String flightNo = ret1.getString(1);
    		int msgswitch = ret1.getInt(2);
    		ret1.close();
			pst.close();
    		if(msgswitch == 1) {
    			System.out.println("++++++++++++++++++ WINNER ++++++++++++++++++");
    			for(String winner: winners) {
    				if(winner != null) {
	    				String sql2 = "SELECT username, tel, biddingPrice FROM userToken INNER JOIN biddingPrice ON userToken.IDcard=biddingPrice.identity WHERE userToken.IDcard=?;";
	    				pst = conn.prepareStatement(sql2);
	    				pst.setString(1, winner);
	    				ret2 = pst.executeQuery();
	    				if(ret2.next()) {
	    					String user = ret2.getString(1);
	    					String tel = ret2.getString(2);
	    					String price = ret2.getString(3);
	    					System.out.println(user);
	    					System.out.println(tel);
	    					System.out.println(price);
	    					// TODO
	    					// TextModule (user, tel, flightNo, price) won the bidding
	    				}
	    				ret2.close();
	    				pst.close();
    				}
    			}
    			System.out.println("++++++++++++++++++ LOSER ++++++++++++++++++");
    			for(String loser: losers) {
    				if(loser != null) {
	    				String sql3 = "SELECT username, tel, biddingPrice FROM userToken INNER JOIN biddingPrice ON userToken.IDcard=biddingPrice.identity WHERE userToken.IDcard=?;";
	    				pst = conn.prepareStatement(sql3);
	    				pst.setString(1, loser);
	    				ret2 = pst.executeQuery();
	    				if(ret2.next()) {
	    					String user = ret2.getString(1);
	    					String tel = ret2.getString(2);
	    					String price = ret2.getString(3);
	    					System.out.println(user);
	    					System.out.println(tel);
	    					System.out.println(price);
	    					// TODO
	    					// TextModule (user, tel, flightNo, price) lose the bidding
	    				}
	    				ret2.close();
	    				pst.close();
    				}
    			}
    		}
    		else {
    			System.out.println("The message sending switch is not enabled");
    		}
    	}
    	else {
    		throw new SQLException();
    	}
	}
}
