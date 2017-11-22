package com.agiview.auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.agiview.auction.passengerFlight.passengerFlightRes;

public class passengerTicket {
	
	private static int TIMEROUND = 60*36;
	
	public passengerTicket() {
		super();
	}
	
	public static String getTimeStampNumberFormat(Timestamp formatTime) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(formatTime);
    }
	
	public static void getTicket(Connection conn, String identity, passengerFlightRes res) throws SQLException, ParseException {
		
		boolean result = getFromLocal(conn, identity, res);
		if(!result) {
			getFromRemote(identity);
			getFromLocal(conn, identity, res);
		}
	}
	
	public static void getOneTicket(Connection conn, String identity, String auctionID, passengerFlightRes res) throws SQLException, ParseException {
		PreparedStatement pst = null;
    	ResultSet ret, ret1, ret2, ret3 = null;
    	ArrayList<flightInfo> flights = new ArrayList<flightInfo>();
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
		String sql = "SELECT flightNo, date FROM auctionManage WHERE auctionID=?;";
		
		pst = conn.prepareStatement(sql);
		pst.setString(1, auctionID);
		ret = pst.executeQuery();
		if(ret.next()) {
			String flightNo = ret.getString(1);
			String date = ret.getString(2);
			ret.close();
			pst.close();
			String sql1 = "SELECT ticketNo, carbin, seat, entrance, onBoard, orgCity, dstCity, depTime, "
	    			+ "arrTime FROM passengerFlight WHERE ID=? AND flightNo=? AND date=?;";
			
			pst = conn.prepareStatement(sql1);
			pst.setString(1, identity);
			pst.setString(2, flightNo);
			pst.setString(3, date);
			ret1 = pst.executeQuery();
			flightInfo fi = new flightInfo();
			if(ret1.next()) {
	            Timestamp depTime = ret1.getTimestamp(8);
	            Timestamp arrTime = ret1.getTimestamp(9);
	            long t_dep, t_arr;
            	t_dep = timeformat.parse(getTimeStampNumberFormat(depTime)).getTime();
            	t_arr = timeformat.parse(getTimeStampNumberFormat(arrTime)).getTime();
	               
            	fi.setFlightNo(flightNo);
            	fi.setTicketNo(ret1.getString(1));
            	fi.setFlightDate(date);
            	fi.setCarbin(ret1.getString(2));
            	fi.setSeat(ret1.getString(3));
            	fi.setEntrance(ret1.getString(4));
            	fi.setOnBoard(ret1.getString(5));
            	fi.setOrgCity(ret1.getString(6));
            	fi.setDstCity(ret1.getString(7));
            	fi.setDepTime((int) (t_dep / 1000));
            	fi.setArrTime((int) (t_arr / 1000));
            	
            	ret1.close();
            	pst.close();
            	ArrayList<auctionInfo> auction = new ArrayList<auctionInfo>();
            	auctionInfo ac = new auctionInfo();
            	ac.setAuctionID(auctionID);
            	String sql2 = "SELECT auctionType, auctionState, startTime, endTime, userStatus, description FROM userStates INNER JOIN auctionParam "
            			+ "ON userStates.auctionID=auctionParam.auctionID WHERE identity=? AND auctionParam.auctionID=?;";
            	
            	pst = conn.prepareStatement(sql2);
            	pst.setString(1, identity);
            	pst.setString(2, auctionID);
            	ret2 = pst.executeQuery();
            	if(ret2.next()) {
            		java.util.Date nowdate = new java.util.Date();
                    Timestamp nowTime = new Timestamp(nowdate.getTime());
            		Timestamp startTime = ret2.getTimestamp(3);
            		Timestamp endTime = ret2.getTimestamp(4);
            		ac.setAuctionType(ret2.getInt(1));
            		ac.setAuctionState(ret2.getInt(2));
            		ac.setUserStatus(ret2.getInt(5));
            		ac.setDescription(ret2.getString(6));
            		long start, end, t_now;
        			t_now = timeformat.parse(getTimeStampNumberFormat(nowTime)).getTime();
        			start = timeformat.parse(getTimeStampNumberFormat(startTime)).getTime();
        			end = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
        			int timeLeft = (int)((end - t_now) / (1000 * 60 * 60)) > 0 ? (int)((end - t_now) / (1000 * 60 * 60)):0;
            		ac.setStartTime((int) (start / 1000));
            		ac.setEndTime((int) (end / 1000));
            		ac.setTimeLeft(timeLeft);
            		
	            	String sql3 = "SELECT biddingPrice FROM auctionResult WHERE auctionID=? AND identity=?;";
	            	pst = conn.prepareStatement(sql3);
	            	pst.setString(1, auctionID);
	            	pst.setString(2, identity);
	            	ret3 = pst.executeQuery();
	            	if(ret3.next()) {
	            		ac.setHit(1);
	            	}
	            	else {
	            		ac.setHit(0);
	            	}
            		auction.add(ac);
            	}
            	fi.setAuctions(auction);
			}
			flights.add(fi);
		}
		res.setFlights(flights);
	}
	
	public static boolean getFromLocal(Connection conn, String identity, passengerFlightRes res) throws SQLException, ParseException {
		PreparedStatement pst = null;
    	ResultSet ret, ret1, ret2, ret3 = null;
    	String auctionID = null;
    	ArrayList<flightInfo> flights = new ArrayList<flightInfo>();
		boolean result = false;
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
    	String sql1 = "SELECT timeStamp, flightNo, ticketNo, date, carbin, seat, entrance, onBoard, orgCity, dstCity, depTime, "
    			+ "arrTime FROM passengerFlight WHERE ID=?;";

    	pst = conn.prepareStatement(sql1);
    	pst.setString(1, identity);
    	ret = pst.executeQuery();
    	while(ret.next()) {
    		result = true;
    		java.util.Date date = new java.util.Date();
            Timestamp nowTime = new Timestamp(date.getTime());
            Timestamp storeTime = ret.getTimestamp(1);
            Timestamp depTime = ret.getTimestamp(11);
            Timestamp arrTime = ret.getTimestamp(12);
            long t_now,t_store, t_dep, t_arr;
        	t_now = timeformat.parse(getTimeStampNumberFormat(nowTime)).getTime();
        	t_store = timeformat.parse(getTimeStampNumberFormat(storeTime)).getTime();
        	t_dep = timeformat.parse(getTimeStampNumberFormat(depTime)).getTime();
        	t_arr = timeformat.parse(getTimeStampNumberFormat(arrTime)).getTime();            
            long timelap = (t_now - t_store) / (60*1000);
        	flightInfo fi = new flightInfo();
            if(timelap < TIMEROUND) {
            	String flightNo = ret.getString(2);
            	String flightDate = ret.getString(4);
            	fi.setFlightNo(flightNo);
            	fi.setTicketNo(ret.getString(3));
            	fi.setFlightDate(flightDate);
            	fi.setCarbin(ret.getString(5));
            	fi.setSeat(ret.getString(6));
            	fi.setEntrance(ret.getString(7));
            	fi.setOnBoard(ret.getString(8));
            	fi.setOrgCity(ret.getString(9));
            	fi.setDstCity(ret.getString(10));
            	fi.setDepTime((int) (t_dep / 1000));
            	fi.setArrTime((int) (t_arr / 1000));
            	
            	ArrayList<auctionInfo> auction = new ArrayList<auctionInfo>();
				String sql2 = "SELECT auctionID FROM auctionManage WHERE flightNo=? AND date=?;";
            	pst = conn.prepareStatement(sql2);
            	pst.setString(1, flightNo);
            	pst.setString(2, flightDate);
            	ret1 = pst.executeQuery();
            	while (ret1.next()) {
            		auctionID = ret1.getString(1);
            		initUserStates(conn, identity, auctionID);
	            	auctionInfo ac = new auctionInfo();
	            	ac.setAuctionID(auctionID);
	            	String sql3 = "SELECT auctionType, auctionState, startTime, endTime, userStatus, description FROM userStates INNER JOIN auctionParam "
	            			+ "ON userStates.auctionID=auctionParam.auctionID WHERE identity=? AND auctionParam.auctionID=?;";
	            	
	            	pst = conn.prepareStatement(sql3);
	            	pst.setString(1, identity);
	            	pst.setString(2, auctionID);
	            	ret2 = pst.executeQuery();
	            	if(ret2.next()) {
	            		Timestamp startTime = ret2.getTimestamp(3);
	            		Timestamp endTime = ret2.getTimestamp(4);
	            		ac.setAuctionType(ret2.getInt(1));
	            		ac.setAuctionState(ret2.getInt(2));
	            		ac.setUserStatus(ret2.getInt(5));
	            		ac.setDescription(ret2.getString(6));
	            		long start, end;
            			start = timeformat.parse(getTimeStampNumberFormat(startTime)).getTime();
            			end = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
	            		int timeLeft = (int)((end - t_now) / (1000 * 60 * 60)) > 0 ? (int)((end - t_now) / (1000 * 60 * 60)):0;
	            		ac.setStartTime((int) (start / 1000));
	            		ac.setEndTime((int) (end / 1000));
	            		ac.setTimeLeft(timeLeft);
	            		
		            	String sql4 = "SELECT biddingPrice FROM auctionResult WHERE auctionID=? AND identity=?;";
		            	pst = conn.prepareStatement(sql4);
		            	pst.setString(1, auctionID);
		            	pst.setString(2, identity);
		            	ret3 = pst.executeQuery();
		            	if(ret3.next()) {
		            		ac.setHit(1);
		            	}
		            	else {
		            		ac.setHit(0);
		            	}
	            		auction.add(ac);
	            	}
            	}
            	fi.setAuctions(auction);
            }
            else {
            	return false;
            }
            flights.add(fi);
    	}
    	res.setFlights(flights);
    	return result;
	}
	
	public static void getFromRemote(String identity) {
		// TODO
		// get the passenger ticket information in skyline system
		
	}
	
	public static void initUserStates(Connection conn, String identity, String auctionID) throws SQLException {
		PreparedStatement pst = null;
		ResultSet ret = null;
		String sql1 = "SELECT userStatus FROM userStates WHERE identity=? AND auctionID=?;";
		pst = conn.prepareStatement(sql1);
		pst.setString(1, identity);
		pst.setString(2, auctionID);
		ret = pst.executeQuery();
		if(!ret.next()) {
			pst.close();
	    	String sql2 = "INSERT INTO userStates (identity, auctionID, userStatus, timeStamp) VALUES (?,?,?,?);";
	    	
	    	java.util.Date date = new java.util.Date();
	        Timestamp nowTime = new Timestamp(date.getTime());
	    	pst = conn.prepareStatement(sql2);
	    	pst.setString(1, identity);
	    	pst.setString(2, auctionID);
	    	pst.setInt(3, 0);
	    	pst.setTimestamp(4, nowTime);
	    	pst.executeUpdate();
		}    	
	}
}