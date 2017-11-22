package com.agiview.member;

import java.io.Serializable;

public class memberIndexParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String action;
	
	public memberIndexParam() {
		super();
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
}
