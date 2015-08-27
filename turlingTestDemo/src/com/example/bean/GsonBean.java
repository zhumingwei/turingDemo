package com.example.bean;

import com.google.gson.annotations.SerializedName;

public class GsonBean {
	
	@SerializedName("code")
	private String code;
	@SerializedName("text")
	private String text;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
