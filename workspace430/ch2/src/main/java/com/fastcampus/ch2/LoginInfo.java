package com.fastcampus.ch2;

public class LoginInfo {
	public boolean isRememberId() {
		return rememberId;
	}
	public void setRememberId(boolean rememberId) {
		this.rememberId = rememberId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	private String id;
	private String password;
	private boolean rememberId;
}
