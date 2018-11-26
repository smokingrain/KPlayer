package com.xk.player.tools;


import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HttpClientTest {
	
	
	public static Map<String,String> getHtml(String content) {
		if(null==content){
			return null;
		}
		Document doc=Jsoup.parse(content);
		Elements links=doc.select("ul[class=searchResult]");
		if(null!=links&&links.size()==1){
			Map<String,String>songs=new HashMap<String,String>();
			links=links.get(0).getElementsByAttributeValue("target", "_blank");
			for(int i=0;i<links.size();i++){
				Element ele=links.get(i);
				String href=ele.attr("href");
				if(href!=null&&href.startsWith("/play/")){
					if(!songs.containsKey(href)){
						songs.put(href, ele.text());
					}
				}
			}
			return songs;
		}
		
		return null;
	
	}

	public static Elements getScripts(String content){
		if(null==content){
			return null;
		}
		Document doc=Jsoup.parse(content);
		Elements scripts=doc.select("script:not([src])");
		return scripts;
	}
	

}
