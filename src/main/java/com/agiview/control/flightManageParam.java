package com.agiview.control;

import java.io.Serializable;

public class flightManageParam implements Serializable {  
    private static final long serialVersionUID = 1L;
    
    private int dayLap;
    
    public flightManageParam() {
    	super();
    }
    
    public void setDayLap(int day) {
    	this.dayLap = day;
    }
    
    public int getDayLap() {
    	return dayLap;
    }
}
