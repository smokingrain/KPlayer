/**
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                 佛祖保佑                                      永无BUG
 * @author xiaokui
 * @版本 ：v1.0
 * @时间：2016-5-2上午10:44:33
 */
package com.xk.player.tools.sources;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xk.player.tools.ByteUtil;
import com.xk.player.tools.HTTPUtil;
import com.xk.player.tools.JSONUtil;
import com.xk.player.tools.LrcInfo;
import com.xk.player.tools.sources.IDownloadSource.SearchInfo;

/**
 * @项目名称：MusicParser
 * @类名称：SongSeacher.java
 * @类描述：
 * @创建人：xiaokui
 * 时间：2016-5-2上午10:44:33
 */
public class SongSeacher {

	
	public static String fromCharCodes(String[]codes){
		if(null==codes){
			return "";
		}
		StringBuilder builder=new StringBuilder();
		for(String code:codes){
			int intValue=Integer.parseInt(code);
			char chr=(char) intValue;
			builder.append(chr);
		}
		return builder.toString();
	}
	
	public static String getArtistFromKuwo(String name){
		String searchUrl=null;
		try {
			searchUrl="http://sou.kuwo.cn/ws/NSearch?type=artist&key="+URLEncoder.encode(name, "utf-8")+"&catalog=yueku2016";
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		String html=HTTPUtil.getInstance("search").getHtml(searchUrl);
		if(!StringUtil.isBlank(html)){
			Document doc=Jsoup.parse(html);
			Elements texts=doc.getElementsByAttribute("lazy_src");
			for(Element ele:texts){
				String alt=ele.attr("alt");
				if(null!=alt&&alt.contains(name.replace(" ", "&nbsp;"))){
					return ele.attr("lazy_src");
				}
			}
		}
		return null;
	}
	
	
	/**
	 * 从html中解析从歌词
	 * @param html
	 * @return
	 * @author xiaokui
	 */
	public static LrcInfo perseFromHTML(String html){
		Document doc=Jsoup.parse(html);
		Elements lrcs=doc.select("script");
		LrcInfo lrc=new LrcInfo();
		Map<Long,String> infos=new HashMap<Long, String>();
		for(Element ele:lrcs){
			String content = ele.html();
			if(null != content) {
				content = content.trim();
				System.out.println(content);
				if(content.startsWith("var lrcList = ")) {
					content = content.replace("var lrcList = ", "");
					List<Map<String, String>> listLrcs = JSONUtil.toBean(content, JSONUtil.getCollectionType(List.class, Map.class));
					for(Map<String, String> lrcLine : listLrcs) {
						String time = lrcLine.get("time");
						double dtime = Double.parseDouble(time);
						long ltime = (long) (dtime*1000);
						String text = lrcLine.get("lineLyric");
						infos.put(ltime, text);
					}
					break;
				}
			}
		}
		lrc.setInfos(infos);
		return lrc;
	}
	
	/**
	 * 用途：快速搜索
	 * @date 2016年11月18日
	 * @param name
	 * @return
	 */
	public static Map<String, String> fastSearch(String name) {
		if(StringUtil.isBlank(name)) {
			return Collections.emptyMap();
		}
		try {
			name = URLEncoder.encode(name, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "http://search.kuwo.cn/r.s?SONGNAME=" + name + "&ft=music&rformat=json&encoding=utf8&rn=8&callback=song&vipver=MUSIC_8.0.3.1&_=" + System.currentTimeMillis();
		String html=HTTPUtil.getInstance("search").getHtml(url);
		if(StringUtil.isBlank(html)) {
			return Collections.emptyMap();
		}
		html = html.replace(";song(jsondata);}catch(e){jsonError(e)}", "").replace("try {var jsondata =", "");
		Map<String, Object> rst = JSONUtil.fromJson(html);
		if(null == rst || rst.isEmpty()) {
			return Collections.emptyMap();
		}
		List<Map<String, String>> list = (List<Map<String, String>>) rst.get("abslist");
		Map<String, String> result = new HashMap<String, String>();
		for(Map<String, String> map : list) {
			result.put(map.get("NAME"), map.get("SONGNAME"));
		}
		return result;
	}
	
	
	
	
	
	
}
