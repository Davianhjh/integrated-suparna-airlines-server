package com.agiview.member;

import java.io.Serializable;

public class memberLoginParam implements Serializable {  
    private static final long serialVersionUID = 1L;
    
    private String memberID;
    private String password;
    
    public memberLoginParam() {
    	super();
    }
    
    public void setMemberID(String memberID) {
    	this.memberID = memberID;
    }
    
    public String getMemberID() {
    	return memberID;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    public String getPassword() {
    	return password;
    }
}
