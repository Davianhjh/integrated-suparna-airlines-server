package com.agiview.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.agiview.control.flightManage.flightManageRes;

public class flightList {
	
	public flightList () {
		super();
	}
	
	public static String getTimeStampNumberFormat(Timestamp formatTime) {
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        return m_format.format(formatTime);
    }
	
	public static void getList(Connection conn, int dayLap, flightManageRes res) throws SQLException {
		
		boolean result = getFromLocal(conn, dayLap, res);
		if(!result) {
			getFromRemote(dayLap);
			getFromLocal(conn, dayLap, res);
		}
	}
	
	public static boolean getFromLocal(Connection conn, int dayLap, flightManageRes res) throws SQLException {
		PreparedStatement pst = null;
    	ResultSet ret1, ret2, ret3= null;
    	ArrayList<flight> flights = new ArrayList<flight>();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
    	SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
    	
    	String sql1 = "SELECT MAX(flightDate), MAX(depTime) FROM flightManage;";
    	pst = conn.prepareStatement(sql1);
    	ret1 = pst.executeQuery();
    	if(ret1.next()) {
    		String borderDate = ret1.getString(1);   
    		Timestamp borderTime = ret1.getTimestamp(2);
    		
    		Date now = new Date();
    		String start = sdf.format(now);
    		Calendar c = Calendar.getInstance();  
            c.setTime(now);
            c.add(Calendar.DAY_OF_MONTH, dayLap);
            String edge = sdf.format(c.getTime());

            if(borderDate.compareTo(edge) >= 0) {   // query days within the local data stored 
            	ret1.close();
            	pst.close();
                c.add(Calendar.DAY_OF_MONTH, 1);
        		String end = sdf.format(c.getTime());
            	String sql2 = "SELECT flightNo, flightDate, orgCity, dstCity, planeType, tripType, depTime, arrTime, state FROM "
            			+ "flightManage WHERE depTime BETWEEN ? AND ?;";
            	
            	pst = conn.prepareStatement(sql2);
            	pst.setString(1, start);
            	pst.setString(2, end);
            	ret2 = pst.executeQuery();
            	while(ret2.next()) {
            		flight flightData = new flight();
            		Timestamp depTime = ret2.getTimestamp(7);
            		Timestamp arrTime = ret2.getTimestamp(8);
            		long t_dep, t_arr;
                    try {
                    	t_dep = timeformat.parse(getTimeStampNumberFormat(depTime)).getTime();
                    	t_arr = timeformat.parse(getTimeStampNumberFormat(arrTime)).getTime();
                    } catch (ParseException e) {
                    	e.printStackTrace();
                    	return false;
                    }
                    String flightNo = ret2.getString(1);
                    String flightDate = ret2.getString(2);
            		flightData.setFlightNo(flightNo);
            		flightData.setFlightDate(flightDate);
            		flightData.setOrgCity(ret2.getString(3));
            		flightData.setDstCity(ret2.getString(4));
            		flightData.setPlaneType(ret2.getString(5));
            		flightData.setTripType(ret2.getString(6));
            		flightData.setDepTime((int) (t_dep / 1000));
            		flightData.setArrTime((int) (t_arr / 1000));
            		flightData.setState(ret2.getInt(9));
            		
            		String sql3 = "SELECT auctionID, startTime, endTime FROM auctionParam WHERE flightNo=? AND flightDate=?;";
            		pst = conn.prepareStatement(sql3);
            		pst.setString(1, flightNo);
            		pst.setString(2, flightDate);
            		ret3 = pst.executeQuery();
            		ArrayList<Percent> proceed = new ArrayList<Percent>();
            		while(ret3.next()) {
            			Percent per = new Percent();
            			java.util.Date date = new java.util.Date();
                        Timestamp nowTime = new Timestamp(date.getTime());            
            			Timestamp startTime = ret3.getTimestamp(2);
            			Timestamp endTime = ret3.getTimestamp(3);
            			long now_t, start_t, end_t;
            			try {
            				start_t = timeformat.parse(getTimeStampNumberFormat(startTime)).getTime();
                        	end_t = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
                        	now_t = timeformat.parse(getTimeStampNumberFormat(nowTime)).getTime();
                        	int percent = (int)((now_t - start_t)*100/(end_t - start_t));
                			per.setAuctionID(ret3.getString(1));
                			per.setPercentage(percent);
                			proceed.add(per);
            			} catch (ParseException e) {
                        	e.printStackTrace();
                        	return false;
                        }            			
            		}
            		flightData.setPercent(proceed);
            		flights.add(flightData);
            	}
            	res.setFlights(flights);
            	return true;
            }
            else {
            	// TODO
            	// get from remote
            	ret1.close();
            	pst.close();
            	String sql2 = "SELECT flightNo, flightDate, orgCity, dstCity, planeType, tripType, depTime, arrTime, state FROM "
            			+ "flightManage WHERE depTime BETWEEN ? AND ?;";
            	
            	pst = conn.prepareStatement(sql2);
            	pst.setString(1, start);
            	pst.setTimestamp(2, borderTime);
            	ret2 = pst.executeQuery();
            	while(ret2.next()) {
            		flight flightData = new flight();
            		Timestamp depTime = ret2.getTimestamp(7);
            		Timestamp arrTime = ret2.getTimestamp(8);
            		long t_dep, t_arr;
                    try {
                    	t_dep = timeformat.parse(getTimeStampNumberFormat(depTime)).getTime();
                    	t_arr = timeformat.parse(getTimeStampNumberFormat(arrTime)).getTime();
                    } catch (ParseException e) {
                    	e.printStackTrace();
                    	return false;
                    }
                    String flightNo = ret2.getString(1);
                    String flightDate = ret2.getString(2);
            		flightData.setFlightNo(flightNo);
            		flightData.setFlightDate(flightDate);
            		flightData.setOrgCity(ret2.getString(3));
            		flightData.setDstCity(ret2.getString(4));
            		flightData.setPlaneType(ret2.getString(5));
            		flightData.setTripType(ret2.getString(6));
            		flightData.setDepTime((int) (t_dep / 1000));
            		flightData.setArrTime((int) (t_arr / 1000));
            		flightData.setState(ret2.getInt(9));
            		
            		String sql3 = "SELECT auctionID, startTime, endTime FROM auctionParam WHERE flightNo=? AND flightDate=?;";
            		pst = conn.prepareStatement(sql3);
            		pst.setString(1, flightNo);
            		pst.setString(2, flightDate);
            		ret3 = pst.executeQuery();
            		ArrayList<Percent> proceed = new ArrayList<Percent>();
            		while(ret3.next()) {
            			Percent per = new Percent();
            			java.util.Date date = new java.util.Date();
                        Timestamp nowTime = new Timestamp(date.getTime());            
            			Timestamp startTime = ret3.getTimestamp(2);
            			Timestamp endTime = ret3.getTimestamp(3);
            			long now_t, start_t, end_t;
            			try {
            				start_t = timeformat.parse(getTimeStampNumberFormat(startTime)).getTime();
                        	end_t = timeformat.parse(getTimeStampNumberFormat(endTime)).getTime();
                        	now_t = timeformat.parse(getTimeStampNumberFormat(nowTime)).getTime();
                        	int percent = (int)((now_t - start_t)*100/(end_t - start_t));
                			per.setAuctionID(ret3.getString(1));
                			per.setPercentage(percent);
                			proceed.add(per);
            			} catch (ParseException e) {
                        	e.printStackTrace();
                        	return false;
                        }            			
            		}
            		flightData.setPercent(proceed);
            		flights.add(flightData);
            	}
            	res.setFlights(flights);
            	return true;          // query days exceed the local data stored, need to get from remote
            }
    	}
    	else return false;
	}
	
	public static void getFromRemote(int dayLap) {
		// TODO
		// get the flightList in flight system
		
	}
}
