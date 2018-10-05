package com.yhh.demo;

public class Valve {

	public void invoke(ValveContext valveContext) {
		valveContext.invokeNext();
	}
}
