package com.agiview.control;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.agiview.HiKariCP.HiKariCPHandler;
import com.agiview.auction.auctionScheduleList;

public class startAuction {
	
	public static ScheduledExecutorService ses;
	public static int threadPoolSize = 10;
	static {
		startAuction.ses = Executors.newScheduledThreadPool(threadPoolSize);
	}
	
	public startAuction () {
		super();
	}

	static class auctionTask implements Runnable {
		private String auctionID;
		public auctionTask (String auctionID) {
			this.auctionID = auctionID;
		}
		@Override
		public void run() {
			Connection conn = null;
			java.util.Date nowDate = new java.util.Date();
            Timestamp endTime = new Timestamp(nowDate.getTime());
			System.out.println(auctionID + " has been ended");
			System.out.println(endTime);
			try {
				conn = HiKariCPHandler.getConn();
				endAuction ea = new endAuction();				
				ea.getAuctionResult(conn, auctionID);
				ea.sendNotification(conn, auctionID);
				ea.sendMsg(conn, auctionID);
				changeAuctionState.updateFlightState(conn, auctionID);
				auctionScheduleList.auctionSchedule.remove(auctionID);
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public int auctionStart(String auctionID, long timeDelay)  {
		try {
			 ScheduledFuture sfa = this.ses.schedule(new auctionTask(auctionID), timeDelay, TimeUnit.SECONDS);
			 auctionScheduleList.auctionSchedule.put(auctionID, sfa);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
