package com.xk.player.tools;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Config implements Cloneable{

	/** 背景图片类型，默认 */
	public Integer BGTYPE=0;
	
	/** 默认背景图片路径 */
	public String BGPATH="/images/bk.png";
	
	/** 0,顺序播放，1，随机播放，2，单曲循环 */
	public int PLAY_MODEL=0;
	
	/** 最大音量 */
	public long maxVolume=100;
	/** 当前音量 */
	public long defaultVolume=15;
	
	/** 歌词背景色RGB (R) */
	public int br=253;
	/** 歌词背景色RGB (G) */
	public int bg=254;
	/** 歌词背景色RGB (B) */
	public int bb=255;
	
	/** 歌词进度色RGB (R) */
	public int cr=22;
	/** 歌词进度色RGB (G) */
	public int cg=136;
	/** 歌词进度色RGB (B) */
	public int cb=227;
	
	/** 桌面歌词背景色RGB (R) */
	public int dbr = 0x00;
	/** 桌面歌词背景色RGB (G) */
	public int dbg = 0xff;
	/** 桌面歌词背景色RGB (B) */
	public int dbb = 0x00;
	
	/** 桌面歌词进度色RGB (R) */
	public int dcr = 0xff;
	/** 桌面歌词进度色RGB (G) */
	public int dcg = 0x00;
	/** 桌面歌词进度色RGB (B) */
	public int dcb = 0x00;
	
	
	
	/** 歌词字体名称 */
	public String fontName="楷体";
	/** 歌词字体样式 */
	public int fontStyle=SWT.NORMAL;
	/** 歌词字体大小 */
	public int fontSize=22;
	
	/** 桌面歌词字体名称 */
	public String dfontName="楷体";
	/** 桌面歌词字体样式 */
	public int dfontStyle=Font.PLAIN;
	/** 桌面歌词字体大小 */
	public int dfontSize=36;
	
	/** 桌面歌词描边 */
	public boolean stroke = false;
	
	/** 歌曲文件下载路径 */
	public String downloadPath=System.getProperty("user.dir");
	/** 歌词文件下载路径 */
	public String lrcPath=System.getProperty("user.dir");
	
	/** 播放列表 */
	public List<String> songList=new ArrayList<String>();
	
	/** 我的最爱列表 */
	public List<String> favoriteList=new ArrayList<String>();

	/** 搜索下载类型 */
	public String searchType = "mp3";
	
	/** 数据源 */
	public String downloadSource = "qier";
	
	/** 是否失效 */
	@JsonIgnore
	private boolean died = false;
	
	/** 临时 */
	@JsonIgnore
	public Map<String,Map<String,String>> maps=new HashMap<String,Map<String,String>>();
	
	private static Config instance;
	public static Config getInstance(){
		if(null==instance){
			File file=new File("config.jc");
			if(file.exists()){
				if(file.isFile()){
					String result=FileUtils.readString(file.getAbsolutePath());
					instance=JSONUtil.toBean(result, Config.class);
				}else{
					file.delete();
				}
			}
			if(null == instance) {
				instance=new Config();
			}
		}
		return instance;
	}
	
	private Config(){
	}
	
	
	public void save(){
		File file=new File("config.jc");
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result=JSONUtil.toJson(this);
		FileUtils.writeString(result, file);
	}

	public boolean isDied() {
		return died;
	}

	@Override
	public Config clone() {
		Config o = null;
		try {
			o = (Config) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	public static void resetConfig(Config config) {
		config.save();
		instance.died=true;
		instance = null;
		getInstance();
	}
	
}
