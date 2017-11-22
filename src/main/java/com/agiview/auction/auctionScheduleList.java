package com.agiview.auction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class auctionScheduleList {
	public static Map<String, ScheduledFuture> auctionSchedule;
	static {
		auctionSchedule = new HashMap<String, ScheduledFuture>();
	}
	
	public auctionScheduleList() {
		super();
	}
}
