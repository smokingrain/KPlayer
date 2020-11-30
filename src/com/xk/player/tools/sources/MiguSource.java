package com.xk.player.tools.sources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;







import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import com.xk.player.lrc.XRCLine;
import com.xk.player.tools.HTTPUtil;
import com.xk.player.tools.HttpClientUtils;
import com.xk.player.tools.JSONUtil;
import com.xk.player.tools.LrcParser;
import com.xk.player.tools.SongLocation;

public class MiguSource implements IDownloadSource{
	private BasicCookieStore cookieStore = new BasicCookieStore();
	private CloseableHttpClient client = HttpClientUtils.createSSLClientDefault(cookieStore);
	

	public static void main(String[] args) {
		new MiguSource().getLrc("偏爱");
	}
	
	@Override
	public List<SearchInfo> getLrc(String name) {
		return getSong(name);
	}

	@Override
	public List<SearchInfo> getMV(String name) {
		HTTPUtil util = HTTPUtil.getInstance("search");
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put("keyword", URLEncoder.encode(name, "UTF-8"));
			params.put("pgc", "1");
			params.put("rows", "25");
			params.put("type", "5");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Collections.emptyList();
		}
		List<SearchInfo> info = new ArrayList<IDownloadSource.SearchInfo>();
		String strRst = util.getHtml("http://m.music.migu.cn/migu/remoting/scr_search_tag", params);
		Map<String, Object> mp = JSONUtil.fromJson(strRst);
		if(null == mp || mp.size() == 0) {
			return info;
		}
		
		List<Map<String, Object>> mvs = (List<Map<String, Object>>) mp.get("mv");
		for(Map<String, Object> mv : mvs) {
			SearchInfo mvInfo = new SearchInfo();
			mvInfo.name = (String) mv.get("songName");
			mvInfo.type = "mv";
			info.add(mvInfo);
		}
		
		return info;
	}

	@Override
	public List<SearchInfo> getSong(String name) {
		return getSong(name, "mp3");
	}

	@Override
	public List<SearchInfo> getSong(String name, String type) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put("keyword", URLEncoder.encode(name, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Collections.emptyList();
		}
		get("https://m.music.migu.cn/v3/search?keyword=" + params.get("keyword"));
		List<SearchInfo> info = new ArrayList<IDownloadSource.SearchInfo>();
		String requestUrl = String.format("https://m.music.migu.cn/migu/remoting/scr_search_tag?rows=20&type=2&keyword=%s&pgc=1", params.get("keyword"));
		String strRst = get(requestUrl);
		Map<String, Object> mp = JSONUtil.fromJson(strRst);
		if(null == mp || mp.size() == 0) {
			return info;
		}
		
		List<Map<String, Object>> musics = (List<Map<String, Object>>) mp.get("musics");
		if(null == musics) {
			return info;
		}
		for(Map<String, Object> music : musics) {
			SearchInfo songInfo = new SearchInfo();
			songInfo.album = (String) music.get("albumName");
			songInfo.name = (String) music.get("songName");
			songInfo.singer = (String) music.get("singerName"); 
			songInfo.type = "mp3";
			songInfo.url = (String) music.get("mp3");
			songInfo.urlFound = false;
			songInfo.lrcUrl = "http://api.migu.jsososo.com/lyric?cid=" + music.get("copyrightId");
			info.add(songInfo);
		}
		
		
		return info;
	}

	@Override
	public Map<String, String> fastSearch(String name) {
		HTTPUtil util = HTTPUtil.getInstance("search");
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put("keyword", URLEncoder.encode(name, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Collections.emptyMap();
		}
		String strRst = util.getHtml("http://m.music.migu.cn/migu/remoting/autocomplete_tag", params);
		Map<String, Object> mp = JSONUtil.fromJson(strRst);
		if(null == mp || mp.size() == 0) {
			return Collections.emptyMap();
		}
		List<Map<String, String>> musics = (List<Map<String, String>>) mp.get("key");
		Map<String, String> map = new HashMap<String, String>();
		for(Map<String, String> info : musics) {
			map.put(info.get("name"), info.get("name"));
		}
		return map;
	}

	@Override
	public String getArtist(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SongLocation getInputStream(String url) {
		return HTTPUtil.getInstance("search").getInputStream(url);
	}

	@Override
	public List<XRCLine> parse(String content) {
		if(null == content) {
			return Collections.emptyList();
		}
		Map<String, Object> json = JSONUtil.fromJson(content);
		if(null == json || json.isEmpty()) {
			return Collections.emptyList();
		}
		String lrc = (String) json.get("data");
		LrcParser parser = new LrcParser((long)Integer.MAX_VALUE);
		StringReader sr = new StringReader(lrc);
		List<XRCLine> lines = parser.parserToXrc(sr);
		return lines;
	}

	@Override
	public boolean tryListenSupport() {
		return false;
	}
	
	private void addCookies(HttpResponse response) {
		Header[] headers = response.getHeaders("set-cookie");
		if(null != headers) {
			for(Header header : headers) {
				cookieStore.addCookie(new BasicClientCookie(header.getName(), header.getValue()));
			}
		}
	}
	
	private String cookieToString() {
		List<Cookie> cookies = cookieStore.getCookies();
		StringBuffer buffer  = new StringBuffer();
		if(null != cookies) {
			for(Cookie cookie : cookies) {
				buffer.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");	
			}
		}
		return buffer.toString();
	}
	
	private void addHeaders(HttpRequestBase httppost) {
		httppost.addHeader("Accept", "*/*");
		httppost.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		httppost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
		httppost.addHeader("Connection", "keep-alive");
		httppost.addHeader("Host", "m.music.migu.cn");
		httppost.addHeader("Origin", "https://m.music.migu.cn/v3");
		httppost.addHeader("Referer", "https://m.music.migu.cn/v3");
		httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36");
		httppost.addHeader("Cookie", cookieToString());
	}
	
	private String get(String url) {
		StringBuffer result=new StringBuffer();
		HttpGet httppost = new HttpGet(url);
		addHeaders(httppost);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();//设置请求和传输超时时间
		httppost.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httppost);  
			addCookies(response);
			HttpEntity entity = response.getEntity();  
			InputStream instream=entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(instream,StandardCharsets.UTF_8));  
			String temp = "";  
			while ((temp = br.readLine()) != null) {  
			    result.append(temp);  
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        
        return result.toString();
	}
	
	private String post(String url, List<BasicNameValuePair> formparams) {
		UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(formparams, StandardCharsets.UTF_8);  
		
		//新建Http  post请求  
		HttpPost httppost = new HttpPost(url);  
		httppost.setEntity(entity1);  
		addHeaders(httppost);
		//处理请求，得到响应  
		CloseableHttpResponse response = null;
		StringBuilder result = new StringBuilder();
		try {
			response = client.execute(httppost);  
			addCookies(response);
			//打印返回的结果  
			HttpEntity entity = response.getEntity();  
			
			if (entity != null) {  
				InputStream instream = entity.getContent();  
				BufferedReader br = new BufferedReader(new InputStreamReader(instream, StandardCharsets.UTF_8));  
				String temp = "";  
				while ((temp = br.readLine()) != null) {  
					result.append(temp);  
				}  
			}  
			httppost.releaseConnection();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(null != response) {
					response.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result.toString();
	}

}
