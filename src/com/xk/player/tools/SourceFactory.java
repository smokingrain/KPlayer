package com.xk.player.tools;

import com.xk.player.tools.sources.IDownloadSource;
import com.xk.player.tools.sources.KugouSource;
import com.xk.player.tools.sources.KuwoSource;
import com.xk.player.tools.sources.MiguSource;
import com.xk.player.tools.sources.NetEasySource;
import com.xk.player.tools.sources.QierSource;

public class SourceFactory {

	
	public static IDownloadSource getSource(String name) {
		if("kuwo".equals(name)) {
			return new KuwoSource();
		} else if("kugou".equals(name)) {
			return new KugouSource();
		} else if("ne".equals(name)) {
			return new NetEasySource();
		} else if("qier".equals(name)) {
			return new QierSource();
		} else if("migu".equals(name)) {
			return new MiguSource();
		}
		return null;
	}
	
}
