package com.xk.player.ui.items;

import com.xk.player.tools.sources.IDownloadSource.SearchInfo;
import com.xk.player.ui.PlayUI;

public class MVSearchItem extends LTableItem {

	public MVSearchItem(SearchInfo info) {
		super(info);
	}

	@Override
	protected void download() {
		String url = info.getUrl();
		if(null != url) {
			PlayUI ui = PlayUI.getInstance();
			ui.playMv(info);
		}
	}

}
