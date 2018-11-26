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

	public Integer BGTYPE=0;//背景图片类型，默认
	public String BGPATH="/images/bk.png";
	
	public int PLAY_MODEL=0;//0,顺序播放，1，随机播放，2，单曲循环
	
	public long maxVolume=100;//最大音量
	public long defaultVolume=15;//当前音量
	
	//歌词背景色
	public int br=253;
	public int bg=254;
	public int bb=255;
	//歌词进度色
	public int cr=22;
	public int cg=136;
	public int cb=227;
	
	//桌面歌词背景色
	public int dbr = 0x00;
	public int dbg = 0xff;
	public int dbb = 0x00;
	
	//桌面歌词进度色
	public int dcr = 0xff;
	public int dcg = 0x00;
	public int dcb = 0x00;
	
	
	
	//歌词字体
	public String fontName="楷体";
	public int fontStyle=SWT.NORMAL;
	
	//桌面歌词字体
	public String dfontName="楷体";
	public int dfontStyle=Font.PLAIN;
	
	//下载路径
	public String downloadPath=System.getProperty("user.dir");
	public String lrcPath=System.getProperty("user.dir");
	
	//播放列表
	public List<String> songList=new ArrayList<String>();
	public List<String> favoriteList=new ArrayList<String>();

	//搜索下载类型
	public String searchType = "mp3";
	
	//数据源
	public String downloadSource = "qier";
	
	@JsonIgnore
	private boolean died = false;
	
	//临时
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
