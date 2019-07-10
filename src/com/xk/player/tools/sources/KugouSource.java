package com.xk.player.tools.sources;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
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

import com.xk.player.lrc.XRCLine;
import com.xk.player.tools.ByteUtil;
import com.xk.player.tools.HTTPUtil;
import com.xk.player.tools.HttpRequestParam;
import com.xk.player.tools.JSONUtil;
import com.xk.player.tools.LrcInfo;
import com.xk.player.tools.LrcParser;
import com.xk.player.tools.SongLocation;

public class KugouSource implements IDownloadSource {

	
	
	@Override
	public List<XRCLine> parse(String content) {
		content = content.substring(42, content.length() -1 );
		Map<String, Object> map = JSONUtil.fromJson(content);
		String lrcText = (String) ((Map<String, Object>)map.get("data")).get("lyrics");
		int length = (int) ((Map<String, Object>)map.get("data")).get("timelength");
		LrcParser parser = new LrcParser((long) length);
		StringReader sr = new StringReader(lrcText);
		List<XRCLine> lines = parser.parserToXrc(sr);
		return lines;
	}

	@Override
	public SongLocation getInputStream(String url) {
		return HTTPUtil.getInstance("player").getInputStream(url);
	}

	@Override
	public List<SearchInfo> getLrc(String name) {
		return getSong(name);
	}

	@Override
	public List<SearchInfo> getMV(String name) {
		List<SearchInfo> songs = new ArrayList<SearchInfo>();
		String searchUrl = null;
		String html = null;
		String callBack = "jQuery19108035724928824395_" + System.currentTimeMillis();
		try {
			searchUrl = "http://mvsearch.kugou.com/mv_search";
			Map<String, String> params = new HashMap<String, String>();
			params.put("callback", callBack);
			params.put("keyword", name);
			params.put("page", "1");
			params.put("pagesize", "30");
			params.put("userid", "-1");
			params.put("clientver", "");
			params.put("platform", "WebFilter");
			params.put("tag", "em");
			params.put("filter", "2");
			params.put("iscorrection", "1");
			params.put("privilege_filter", "0");
			params.put("_", String.valueOf(System.currentTimeMillis()));
			html = HTTPUtil.getInstance("search").getHtml(searchUrl, params);
		} catch (Exception e) {
			return songs;
		}
		if(!StringUtil.isBlank(html)){
			String json = html.substring(callBack.length() + 1, html.length() - 1 );
			Map<String, Object> rst = JSONUtil.fromJson(json);
			Map<String, Object> data = (Map<String, Object>) rst.get("data");
			List<Map<String, Object>> lists = (List<Map<String, Object>>) data.get("lists");
			for(Map<String, Object> minfo : lists) {
				SearchInfo info = new SearchInfo(){

					@Override
					public String getUrl() {
						if(this.urlFound) {
							return url;
						}
						String md5 = ByteUtil.MD5(this.url + "kugoumvcloud");
						String url = "http://trackermv.kugou.com/interface/index/cmd=100&hash=" + this.url + "&key=" + md5 + "&pid=6&ext=mp4&ismp3=0";
						String rst = HTTPUtil.getInstance("test").getHtml(url);
						Map<String, Object> map = JSONUtil.fromJson(rst);
						this.url = (String)((Map<String , Map<String, Object>>)map.get("mvdata")).get("sd").get("downurl");
						this.flashVars.put("url", this.url);
						this.urlFound = true;
						return this.url;
					}

					@Override
					public String getLrcUrl() {
						return "";
					}
					
				};
				songs.add(info);
				info.type= "mv";
				info.album = "";
				info.singer = (String) minfo.get("SingerName");
				info.url = (String) minfo.get("MvHash");
				info.name = ((String) minfo.get("MvName")).replace("<em>", "").replace("</em>", "");
			}
		}
		return songs;
	}

	@Override
	public List<SearchInfo> getSong(String name) {
		return getSong(name, "mp3");
	}

	@Override
	public List<SearchInfo> getSong(String name, String type) {
		List<SearchInfo> songs = new ArrayList<SearchInfo>();
		String searchUrl = null;
		String html = null;
		String callBack = "jQuery191040681639104150236_" + System.currentTimeMillis();
		try {
			searchUrl = "http://songsearch.kugou.com/song_search_v2";
			Map<String, String> params = new HashMap<String, String>();
			params.put("callback", callBack);
			params.put("keyword", URLEncoder.encode(name, "UTF-8"));
			params.put("page", "1");
			params.put("pagesize", "30");
			params.put("userid", "-1");
			params.put("clientver", "");
			params.put("platform", "WebFilter");
			params.put("tag", "em");
			params.put("filter", "2");
			params.put("iscorrection", "1");
			params.put("privilege_filter", "0");
			params.put("_", String.valueOf(System.currentTimeMillis()));
			html = HTTPUtil.getInstance("search").getHtml(searchUrl, params);
		} catch (Exception e) {
			return songs;
		}
		if(!StringUtil.isBlank(html)){
			String json = html.substring(callBack.length() + 1, html.length() - 1 );
			Map<String, Object> rst = JSONUtil.fromJson(json);
			Map<String, Object> data = (Map<String, Object>) rst.get("data");
			List<Map<String, Object>> lists = (List<Map<String, Object>>) data.get("lists");
			for(final Map<String, Object> minfo : lists) {
				SearchInfo info = new SearchInfo(){
					
					@Override
					public String getUrl() {
						if(this.urlFound) {
							return url;
						}
						String url = "http://www.kugou.com/yy/index.php";
						List<HttpRequestParam> params = new ArrayList<HttpRequestParam>();
//						Map<String, String> params = new HashMap<>();
						String callBack = "jQuery191040681639104150256_" + System.currentTimeMillis();
						params.add(HttpRequestParam.put("r", "play/getdata"));
						params.add(HttpRequestParam.put("platid", "4"));
						params.add(HttpRequestParam.put("dfid", "4Vyhka0JsPzT0DLMy10TfJPj"));
						params.add(HttpRequestParam.put("mid", "122dc1e8e26152d6ec1aca669ca448d3"));
						params.add(HttpRequestParam.put("callback", callBack));
						params.add(HttpRequestParam.put("hash", this.url));
						params.add(HttpRequestParam.put("album_id", "" + minfo.get("AlbumID")));
						params.add(HttpRequestParam.put("_", String.valueOf(System.currentTimeMillis())));
						String rst = HTTPUtil.getInstance("search").getHtml(url, params);
						if(null != rst) {
							rst = rst.substring(callBack.length() + 1, rst.length() - 1 );
						}
						Map<String, Object> map = JSONUtil.fromJson(rst);
						this.urlFound = true;
						this.url = (String) ((Map<String, Object>)map.get("data")).get("play_url");
						return this.url;
					}

					@Override
					public String getLrcUrl() {
						if(this.urlFound) {
							return lrcUrl;
						}
						String url = "http://www.kugou.com/yy/index.php";
						List<HttpRequestParam> params = new ArrayList<HttpRequestParam>();
//						Map<String, String> params = new HashMap<>();
						String callBack = "jQuery191040681639104150256_" + System.currentTimeMillis();
						params.add(HttpRequestParam.put("r", "play/getdata"));
						params.add(HttpRequestParam.put("platid", "4"));
						params.add(HttpRequestParam.put("dfid", "4Vyhka0JsPzT0DLMy10TfJPj"));
						params.add(HttpRequestParam.put("mid", "122dc1e8e26152d6ec1aca669ca448d3"));
						params.add(HttpRequestParam.put("callback", callBack));
						params.add(HttpRequestParam.put("hash", this.url));
						params.add(HttpRequestParam.put("album_id", "" + minfo.get("AlbumID")));
						params.add(HttpRequestParam.put("_", String.valueOf(System.currentTimeMillis())));
						if(null != params) {
							StringBuffer sb = new StringBuffer();
							sb.append("?");
							for(HttpRequestParam param : params) {
								sb.append(param.key).append("=").append(param.value).append("&");
							}
							url += sb.toString();
						}
						lrcUrl = url;
						this.urlFound = true;
						return lrcUrl;
					}
				};
				songs.add(info);
				info.type = "mp3";
				info.album = (String) minfo.get("AlbumName");
				info.singer = (String) minfo.get("SingerName");
				info.name = ((String) minfo.get("SongName")).replace("<em>", "").replace("</em>", "");
				info.url = "" + minfo.get("FileHash");
			}
		}
		return songs;
	}

	@Override
	public Map<String, String> fastSearch(String name) {
		Map<String, String> infos = new HashMap<>(); 
		String url = "http://searchtip.kugou.com/getSearchTip";
		Map<String, String> params = new HashMap<>();
		String callBack = "jQuery191040681639104150236_" + System.currentTimeMillis();
		params.put("callback", callBack);
		params.put("MusicTipCount", "5");
		params.put("MVTipCount", "2");
		params.put("albumcount", "2");
		params.put("keyword", name);
		params.put("_", String.valueOf(System.currentTimeMillis()));
		String html = HTTPUtil.getInstance("search").getHtml(url, params);
		if(!StringUtil.isBlank(html)){
			String json = html.substring(callBack.length() + 1, html.length() - 1 );
			Map<String, Object> rst = JSONUtil.fromJson(json);
			List<Map<String, Object>> tips = (List<Map<String, Object>>) rst.get("data");
			for(Map<String, Object> records : tips) {
				if("".equals(records.get("LableName"))) {
					List<Map<String, Object>> datas = (List<Map<String, Object>>) records.get("RecordDatas");
					for(Map<String, Object> obj : datas) {
						infos.put((String)obj.get("HintInfo"), (String)obj.get("HintInfo"));
					}
				}
			}
		}
		return infos;
	}

	@Override
	public String getArtist(String name) {
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

	@Override
	public boolean tryListenSupport() {
		return false;
	}

}
