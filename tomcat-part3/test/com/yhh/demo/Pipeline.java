package com.yhh.demo;

import org.apache.catalina.Valve;


public interface Pipeline {

	
	
	public void invoke();
	
	public void setBasic(Valve valve);
	
	public void addValve(Valve valve);
	
	
	
}
