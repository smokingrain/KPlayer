package com.xk.player.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {

	public static void main(String[] args) {
		
		String reg = "var mv_hash = \".+\";";
		String test = "gdsfgsdgvar mv_hash = \"869270221B2FEDCDF5BB75016C692AF3\";sdfg";
		Pattern pa = Pattern.compile(reg);
		Matcher ma = pa.matcher(test);
		if(ma.find()) {
			String result = ma.group();
			System.out.println(result);
		}
		

	}

}
