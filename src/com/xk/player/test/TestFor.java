package com.xk.player.test;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.StringUtil;

public class TestFor {

	public static void main(String[] args) {
		List<String> numbers = new ArrayList<String>();
		for(int i = 0; i < 1045; i++) {
			numbers.add("" + i);
		}
		
		for(int i = 0; i < numbers.size(); i += 100) {
			List<String> sub = numbers.subList(i, (i + 100) > numbers.size() ? numbers.size() : (i + 100));
			String result = StringUtil.join(sub, ",");
			System.out.println(result);
		}
	}

}
