package com.agiview.visitor;

import java.io.Serializable;

public class visitorIndexParam implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String tel;
	private String idcard;
	
	public visitorIndexParam () {
		super();
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public String getTel() {
		return tel;
	}
	
	public void setIDcard(String IDcard) {
		this.idcard = IDcard;
	}
	
	public String getIDcard() {
		return idcard;
	}
}
