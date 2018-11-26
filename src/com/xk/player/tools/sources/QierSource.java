package com.xk.player.tools.sources;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicHeader;

import sun.misc.BASE64Decoder;

import com.xk.player.lrc.XRCLine;
import com.xk.player.tools.HTTPUtil;
import com.xk.player.tools.JSONUtil;
import com.xk.player.tools.LrcParser;
import com.xk.player.tools.SongLocation;
import com.xk.player.tools.sources.IDownloadSource.SearchInfo;

public class QierSource implements IDownloadSource {

	@Override
	public List<SearchInfo> getLrc(String name) {
		List<SearchInfo> songs = new ArrayList<SearchInfo>();
		List<Map<String, Object>> list = search(name, "song", "0");
		if(null == list) {
			return songs;
		}
		for(Map<String, Object> map : list) {
			SearchInfo info = new SearchInfo() {

				@Override
				public String getUrl() {
					return getLrcUrl();
				}

				@Override
				public String getLrcUrl() {
					if(urlFound) {
						return url;
					}
					headers = new Header[]{new BasicHeader("referer", "https://y.qq.com/portal/player.html")};
					url = String.format("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=%s&songmid=%s&g_tk=5381&jsonpCallback=MusicJsonCallback_lrc&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", System.currentTimeMillis() + "", url);
					urlFound = true;
					return url;
				}
				
			};
			info.name = ((String) map.get("songname"));
			info.type = "mp3";
			info.length = Long.valueOf(Long.parseLong(map.get("interval")
					.toString()) * 1000L * 1000L);
			info.album = ((String) map.get("albumname"));
			List<Map<String, Object>> singer = (List) map.get("singer");
			if ((singer != null) && (singer.size() >= 1)) {
				info.singer = ((String) ((Map) singer.get(0)).get("name"));
			}
			info.url = ((String) map.get("songmid"));
			songs.add(info);
		}
		return songs;
	}

	@Override
	public List<SearchInfo> getMV(String name) {
		List<SearchInfo> songs = new ArrayList<SearchInfo>();
		List<Map<String, Object>> list = search(name, "mv", "12");
		if(null == list) {
			return songs;
		}
		for(Map<String, Object> map : list) {
			SearchInfo info = new SearchInfo() {

				@Override
				public String getLrcUrl() {
					return lrcUrl;
				}
				
			};
			info.name = (String) map.get("mv_name");
			info.singer = (String) map.get("singer_name");
			info.type = "mv";
			info.swfUrl = "https://imgcache.qq.com/tencentvideo_v1/playerv3/TPout.swf?max_age=86400&v=" + System.currentTimeMillis();
			info.flashVars.clear();
			info.flashVars.put("vid", (String)map.get("v_id"));
			info.flashVars.put("autoplay", "1");
			info.flashVars.put("volume", "50");
			info.flashVars.put("searchbar", "0");
			info.flashVars.put("showcfg", "1");
			info.flashVars.put("showend", "1");
			info.flashVars.put("openbc", "1");
			info.flashVars.put("list", "2");
			info.flashVars.put("pay", "0");
			info.flashVars.put("canreplay", "0");
			info.flashVars.put("shownext", "0");
			info.flashVars.put("share", "1");
			info.flashVars.put("bullet", "0");
			info.flashVars.put("theater", "0");
			info.flashVars.put("skin", "https://imgcache.qq.com/minivideo_v1/vd/res/skins/TencentPlayerOutSkinV5.swf");
			info.flashVars.put("switch2h5", "0");
			info.flashVars.put("bulletinput", "0");
			info.flashVars.put("attstart", "");
			info.flashVars.put("defnpayver", "0");
			info.flashVars.put("fmt", "auto");
			info.flashVars.put("vstart", "0");
			info.flashVars.put("ptag", "y_qq_com");
			info.flashVars.put("guid", "3c8beccf47b4863554b3c30e2f18e37f");
			info.flashVars.put("mbid", "&ch%2F" + map.get("v_id") + ".html");
			info.flashVars.put("pageInitTime", "");
			info.flashVars.put("playerInitTime", "" + System.currentTimeMillis());
			info.flashVars.put("fakefull", "0");
			info.flashVars.put("playertype", "6");
			info.flashVars.put("adext", "");
			info.flashVars.put("rcd_info", "");
			songs.add(info);
		}
		return songs;
	}

	@Override
	public List<SearchInfo> getSong(String name) {
		return getSong(name, "mp3");
	}

	private List<Map<String, Object>> search(String name, String type, String searchType) {
		String url = "http://i.y.qq.com/s.music/fcgi-bin/search_for_qq_cp";
	    Map<String, String> params = new HashMap();
	    params.put("g_tk", "938407465");
	    params.put("uin", "0");
	    params.put("format", "jsonp");
	    params.put("inCharset", "utf-8");
	    params.put("outCharset", "utf-8");
	    params.put("notice", "0");
	    params.put("platform", "h5");
	    params.put("needNewCode", "1");
	    params.put("w", name);
	    params.put("zhidaqu", "1");
	    params.put("catZhida", "1");
	    params.put("t", searchType);
	    params.put("flag", "1");
	    params.put("ie", "utf-8");
	    params.put("sem", "1");
	    params.put("sem", "1");
	    params.put("aggr", "0");
	    params.put("perpage", "20");
	    params.put("n", "20");
	    params.put("p", "0");
	    params.put("remoteplace", "txt.mqq.all");
	    params.put("_", System.currentTimeMillis() + "");
	    String jsonpCallback = "searchCallbacksong" + random4Num(4);
	    params.put("jsonpCallback", jsonpCallback);
	    Header[] hs = { new BasicHeader("Origin", "http://y.qq.com/"), new BasicHeader("Referer", "http://y.qq.com/") };
	    String rst = HTTPUtil.getInstance("search").getHtml(url, params, hs);
	    if(null != rst && rst.startsWith(jsonpCallback)) {
			rst = rst.substring(jsonpCallback.length() + 1, rst.length() - 1);
			Map<String, Object> result = JSONUtil.fromJson(rst);
			Map<String, Object> data = (Map<String, Object>) result.get("data");
			if(null == data) {
				return null;
			}
			Map<String, Object> song = (Map<String, Object>) data.get(type);
			if(null == song) {
				return null;
			}
			List<Map<String, Object>> list = (List<Map<String, Object>>) song.get("list");
			return list;
		}
		return null;
	}
	
	@Override
	public List<SearchInfo> getSong(String name, String type) {
		List<SearchInfo> songs = new ArrayList<SearchInfo>();
		List<Map<String, Object>> list = search(name, "song", "0");
		if(null == list) {
			return songs;
		}
		for(Map<String, Object> map : list) {
			SearchInfo info = new SearchInfo() {

				@Override
				public String getUrl() {
					if (urlFound) {
						return url;
					}
					Header[] hs = {
							new BasicHeader("Origin", "http://y.qq.com/"),
							new BasicHeader("Referer", "http://y.qq.com/") };
					String guid = String
							.valueOf(Math.floor(Math.random() * 1.0E9D));
					String tracyUrl = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg";
					Map<String, String> tracyParams = new HashMap();
					tracyParams.put("g_tk", "195219765");
					String jsonpCallback = "MusicJsonCallback"
							+ QierSource.this.random4Num(4);
					tracyParams.put("jsonpCallback", jsonpCallback);
					tracyParams.put("callback", jsonpCallback);
					tracyParams.put("loginUin", "0");
					tracyParams.put("hostUin", "0");
					tracyParams.put("format", "json");
					tracyParams.put("inCharset", "utf-8");
					tracyParams.put("outCharset", "utf-8");
					tracyParams.put("notice", "0");
					tracyParams.put("platform", "yqq");
					tracyParams.put("needNewCode", "0");
					tracyParams.put("cid", "205361747");
					tracyParams.put("uin", "1297716249");
					tracyParams.put("songmid", url);
					tracyParams.put("filename", "C400" + url + ".m4a");
					tracyParams.put("guid", guid);
					tracyParams.put("guid", guid);
					String text = HTTPUtil.getInstance("search").getHtml(
							tracyUrl, tracyParams, hs);
					text = text.replace(jsonpCallback, "").trim();
					text = text.substring(1, text.length() - 1);
					Map<String, Object> map = JSONUtil.fromJson(text);
					String vkey = (String) ((Map) ((List) ((Map) map
							.get("data")).get("items")).get(0)).get("vkey");
					lrcUrl = String
							.format("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=%s&songmid=%s&g_tk=5381&jsonpCallback=MusicJsonCallback_lrc&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0",
									new Object[] { System.currentTimeMillis(),
											url });

					url = String.format("http://dl.stream.qqmusic.qq.com/M800%s.mp3?vkey=%s&guid=%s&fromtag=53",
									new Object[] { url, vkey, guid });

					headers = hs;
					urlFound = true;
					return url;
				}

				@Override
				public String getLrcUrl() {
					if(urlFound) {
						return lrcUrl;
					}
					headers = new Header[]{new BasicHeader("referer", "https://y.qq.com/portal/player.html")};
					lrcUrl = String.format("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=%s&songmid=%s&g_tk=5381&jsonpCallback=MusicJsonCallback_lrc&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", System.currentTimeMillis() + "", url);
					return lrcUrl;
				}
				
			};
			info.name = ((String) map.get("songname"));
			info.type = "mp3";
			info.length = Long.valueOf(Long.parseLong(map.get("interval")
					.toString()) * 1000L * 1000L);
			info.album = ((String) map.get("albumname"));
			List<Map<String, Object>> singer = (List) map.get("singer");
			if ((singer != null) && (singer.size() >= 1)) {
				info.singer = ((String) ((Map) singer.get(0)).get("name"));
			}
			info.url = ((String) map.get("songmid"));
			songs.add(info);
		}
		return songs;
	}
	
	private String random4Num(int num) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for(int i = 0; i < num; i++) {
			int next = random.nextInt(8) + 1;
			sb.append(next);
		}
		return sb.toString();
	}

	@Override
	public Map<String, String> fastSearch(String name) {
		Map<String, Object> map = fast(name);
		if(null == map) {
			return null;
		}
		Map<String, Object> data = (Map<String, Object>) map.get("data");
		if(null == data) {
			return null;
		}
		Map<String, Object> songs = (Map<String, Object>) data.get("song");
		if(null == songs) {
			return null;
		}
		List<Map<String, Object>> list = (List<Map<String, Object>>) songs.get("itemlist");
		if(null == list) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for(Map<String, Object> item : list) {
			result.put(item.get("name").toString(), item.get("name").toString());
		}
		return result;
	}
	
	private Map<String, Object> fast(String name) {
		String callBack = "SmartboxKeysCallbackmod_search" + random4Num(4);
		String url = null;
		try {
			url = String.format("https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?is_xml=0&format=jsonp&key=%s&g_tk=5381&jsonpCallback=%s&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", URLEncoder.encode(name, "UTF-8"), callBack);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		String rst = HTTPUtil.getInstance("search").getHtml(url);
		if(null != rst && rst.startsWith(callBack)) {
			rst = rst.replace(callBack, "");
			rst = rst.substring(1, rst.length() - 1).trim();
			Map<String, Object> map = JSONUtil.fromJson(rst);
			return map;
		}
		return null;
	}

	@Override
	public String getArtist(String name) {
		if(null == name) {
			return null;
		}
		Map<String, Object> map = fast(name);
		if(null == map) {
			return null;
		}
		Map<String, Object> data = (Map<String, Object>) map.get("data");
		if(null == data) {
			return null;
		}
		Map<String, Object> singers = (Map<String, Object>) data.get("singer");
		if(null == singers) {
			return null;
		}
		List<Map<String, Object>> list = (List<Map<String, Object>>) singers.get("itemlist");
		if(null == list) {
			return null;
		}
		for(Map<String, Object> item : list) {
			if(name.equals(item.get("name"))) {
				return (String) item.get("pic");
			}
		}
		return null;
	}

	@Override
	public SongLocation getInputStream(String url) {
		return HTTPUtil.getInstance("player").getInputStream(url);
	}

	@Override
	public List<XRCLine> parse(String content) {
		if(null != content && content.startsWith("MusicJsonCallback_lrc(")) {
			content = content.replace("MusicJsonCallback_lrc(", "");
			content = content.substring(0, content.length() - 1);
			try {
				Map<String, Object> map = JSONUtil.fromJson(content);
				if(null != map) {
					String lrc = (String)map.get("lyric");
					if(null == lrc) {
						return null;
					}
					content = new String(new BASE64Decoder().decodeBuffer(lrc), StandardCharsets.UTF_8);
					StringReader in = new StringReader(content);
					return new LrcParser((long)Integer.MAX_VALUE).parserToXrc(in);
				}
			} catch (IOException e) {
				return null;
			}
			
		}
		return null;
	}

	@Override
	public boolean tryListenSupport() {
		return true;
	}

}
