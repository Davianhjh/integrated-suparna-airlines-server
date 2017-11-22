package com.agiview.control;

import java.io.Serializable;

public class adminLoginParam implements Serializable {  
    private static final long serialVersionUID = 1L;
    
    private String admin;
    private String password;
    
    public adminLoginParam() {
    	super();
    }
    
    public void setAdmin(String adminID) {
    	this.admin= adminID;
    }
    
    public String getAdmin() {
    	return admin;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    public String getPassword() {
    	return password;
    }
}