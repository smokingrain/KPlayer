package com.xk.player.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xk.player.lrc.XRCLine;
import com.xk.player.lrc.XRCNode;

public class LRCFactory {

	public static List<XRCLine> fromTargetFile(String path, long allLength) {
		if(path.endsWith(".zlrc")) {
			String data = FileUtils.readString(path);
			return JSONUtil.toBean(data, JSONUtil.getCollectionType(List.class, XRCLine.class));
		} else if(path.endsWith(".krc")) {
			return KrcText.fromKRC(path);
		} else if(path.endsWith(".trc")) {
			return fromTRC(new File(path));
		} else if(path.endsWith(".lrc")) {
			try {
				LrcParser parser = new LrcParser(allLength);
				return parser.parser(path);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	public static List<XRCLine> fromFile(String musicName, long allLength) {
		Config config = Config.getInstance();
		File songWord = new File(config.lrcPath, musicName + ".lrc");
		File xrcWord = new File(config.lrcPath, musicName + ".zlrc");
		File krcWord = new File(config.lrcPath, musicName + ".krc");
		File trcWord = new File(config.lrcPath, musicName + ".trc");
		if(xrcWord.exists()) {
			String data = FileUtils.readString(xrcWord.getAbsolutePath());
			return JSONUtil.toBean(data, JSONUtil.getCollectionType(List.class, XRCLine.class));
		} else if(krcWord.exists()) {
			return KrcText.fromKRC(krcWord.getAbsolutePath());
		} else if(trcWord.exists()) {
			return fromTRC(trcWord);
		} else if(songWord.exists()) {
			try {
				LrcParser parser = new LrcParser(allLength);
				return parser.parser(songWord.getAbsolutePath());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	private static List<XRCLine> fromTRC(File file) {
		List<XRCLine> lines = new ArrayList<XRCLine>();
		String data = FileUtils.readString(file);
		StringReader reader = new StringReader(data);
		BufferedReader br = new BufferedReader(reader);
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				XRCLine xline = new XRCLine();
				String regLine = "\\[[0-9]{2}:[0-9]{2}\\.[0-9]{2,3}\\]";
				// 编译
				Pattern pattern = Pattern.compile(regLine);
				Matcher matcher = pattern.matcher(line);
				if(matcher.find()) {
					String msg = matcher.group();
					xline.start = strToLong(msg);
					lines.add(xline);
				}
				String regNode = "\\<[0-9]*\\>(\\w+'*\\w*\\s*|[\u4E00-\u9FA5]{1})";
				Pattern patternNode = Pattern.compile(regNode);
				Matcher matcherNode = patternNode.matcher(line);
				long start = 0l;
				while (matcherNode.find()) {
					int groupCount = matcherNode.groupCount();
					for (int i = 0; i < groupCount; i++) {
						String msg = matcherNode.group(i);
						XRCNode node = new XRCNode();
						int last = msg.indexOf(">") + 1;
						node.word = msg.substring(last, msg.length());
						msg = msg.substring(0, last).replace("<", "").replace(">", "");
						node.start = start;
						node.length = Long.parseLong(msg);
						start += node.length;
						xline.nodes.add(node);
					}
				}
				xline.length = start;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	private static long strToLong(String timeStr) {   
        // 因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位   
        // 1:使用：分割 2：使用.分割   
    	timeStr=timeStr.replace("[", "").replace("]", "");
        String[] s = timeStr.split(":");   
        int min = Integer.parseInt(s[0]);   
        String[] ss = s[1].split("\\.");   
        int sec = Integer.parseInt(ss[0]); 
        int mill = Integer.parseInt(ss[1]);
        return min * 60 * 1000 + sec * 1000 + mill;   
    }
}
