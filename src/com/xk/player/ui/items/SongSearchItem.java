package com.xk.player.ui.items;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.xk.player.tools.Config;
import com.xk.player.tools.SongLocation;
import com.xk.player.tools.Util;
import com.xk.player.tools.sources.IDownloadSource.SearchInfo;
import com.xk.player.tools.SourceFactory;
import com.xk.player.ui.PlayUI;
import com.xk.player.uilib.MyList;

public class SongSearchItem extends LTableItem {

	public SongSearchItem(SearchInfo info) {
		super(info);
	}

	
	
	
	@Override
	public boolean oncliek(MouseEvent e, int itemHeight, int index, int type) {
		if(type == MyList.CLICK_DOUBLE && !head) {
			PlayUI.getInstance().addTryListen(info);
			return true;
		}
		//右键菜单
		if(type == MyList.CLICK_DOUBLE && !head && SourceFactory.getSource(info.getSource()).tryListenSupport()) {
			Menu m=new Menu(getParent());
			Menu menu=getParent().getMenu();
			if (menu != null) {
				menu.dispose();
			}
			MenuItem miPlay=new MenuItem(m, SWT.NONE);
			miPlay.setText("试听");
			miPlay.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					PlayUI.getInstance().addTryListen(info);
				}
				
			});
			getParent().setMenu(m);
			m.setVisible(true);
		}
		return super.oncliek(e, itemHeight, index, type);
	}




	@Override
	protected void download() {
		if(downloading){
			return;
		}
		downloading=true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Config conf = Config.getInstance();
				String url = info.getUrl();
				SongLocation loc = SourceFactory.getSource(info.getSource()).getInputStream(url);
				if(null == loc) {
					downloadFailed("下载失败，可能是版权问题导致。");
					downloading=false;
					flush();
					return;
				}
				String parent = conf.downloadPath;
				if(null == parent || parent.trim().isEmpty()){
					parent = "e:/download";
					conf.downloadPath = parent;
					conf.lrcPath = parent;
				}
				boolean succ = true;
				File file = new File(conf.downloadPath, info.singer + " - " + info.name + "." + info.type);
				String realPath = file.getAbsolutePath();
				if(!file.exists()){
					FileOutputStream out = null;
					try {
						file.createNewFile();
						out = new FileOutputStream(file);
						long all=0;
						byte[] buf = new byte[20480];
						int len = 0;
						while((len = loc.input.read(buf, 0, buf.length)) >= 0){
							all += len;
							double per = (double)all / loc.length * 100;
							if(per - persent > 1 || per >= 100){
								persent = (int) (per);
								flush();
							}
							out.write(buf, 0, len);
							out.flush();
						}
					} catch (Exception e) {
						System.out.println("download failed!"+e.getMessage());
						succ = false;
					}finally{
						if(null!=out){
							try {
								out.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if("m4a".equals(info.type)) {
						realPath = new File(conf.downloadPath, info.singer + " - " + info.name + ".mp3").getAbsolutePath();
						succ = Util.changeLocalSourceToMp3(file.getAbsolutePath(), realPath);
						file.delete();
					}
				}
				try {
					loc.input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(succ) {
					PlayUI.getInstance().addFile(realPath,false);
				} else {
					downloadFailed("下载失败，可能是版权问题导致。");
				}
				
				persent=0;
				downloading=false;
				flush();
				
			}
		}).start();

	}

}
