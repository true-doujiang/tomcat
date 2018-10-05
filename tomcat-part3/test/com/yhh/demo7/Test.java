package com.yhh.demo7;

import java.sql.Timestamp;

public class Test {

	public static void main(String[] args) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tsString = ts.toString().substring(0, 19);
        String tsDate = tsString.substring(0, 10);
        System.out.println(tsDate);
	}

}
