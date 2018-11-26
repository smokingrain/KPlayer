package com.xk.player.ui.items;

import static com.xk.player.core.BasicPlayerEvent.PAUSED;
import static com.xk.player.core.BasicPlayerEvent.PLAYING;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jsoup.helper.StringUtil;

import com.xk.player.core.BasicPlayer;
import com.xk.player.core.BasicPlayerException;
import com.xk.player.lrc.XRCLine;
import com.xk.player.tools.Config;
import com.xk.player.tools.HTTPUtil;
import com.xk.player.tools.LRCFactory;
import com.xk.player.tools.SWTTools;
import com.xk.player.tools.SongLocation;
import com.xk.player.tools.SourceFactory;
import com.xk.player.tools.WriteOnReadInputStream;
import com.xk.player.tools.sources.IDownloadSource.SearchInfo;
import com.xk.player.ui.PlayUI;
import com.xk.player.uilib.BaseBox;
import com.xk.player.uilib.DelMusicComp;
import com.xk.player.uilib.ICallback;

public class TryListenItem extends SongItem {
	
	private SearchInfo info;
	
	private File realFile = null;
	private List<XRCLine> lrcs;

	public TryListenItem(Map<String, String> property, SearchInfo info) {
		super(property);
		this.info = info;
	}
	
	
	
	
	@Override
	public boolean oncliek(MouseEvent e, int itemHeight, int index, int type) {
		if(e.button==3){
			Menu m=new Menu(getParent());
			Menu menu=getParent().getMenu();
			if (menu != null) {
				menu.dispose();
			}
			MenuItem miPlay=new MenuItem(m, SWT.NONE);
			miPlay.setText("播放");
			miPlay.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					getParent().select(TryListenItem.this,false);
				}
				
			});
			
			MenuItem linkLrc = new MenuItem(m, SWT.NONE);
			linkLrc.setText("关联歌词");
			linkLrc.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					FileDialog dialog = new FileDialog(getParent().getShell());
					dialog.setText("选择歌词文件");
					dialog.setFilterExtensions(new String[]{"*.zlrc;*.krc;*.lrc;*.trc"});
					dialog.setFilterNames(new String[]{"歌词文件"});
					String path = dialog.open();
					if(!StringUtil.isBlank(path)) {
						lrcs = LRCFactory.fromTargetFile(path, info.length);
					}
				}
			});
			
			
			MenuItem miDel = new MenuItem(m, SWT.NONE);
			miDel.setText("删除");
			miDel.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					BaseBox bb=new BaseBox(getParent().getShell(), SWT.NO_TRIM);
					bb.getShell().setSize(300, 130);
					SWTTools.centerWindow(bb.getShell());
					DelMusicComp comp = new DelMusicComp(bb.getShell(), SWT.NONE);
					comp.setTarget(TryListenItem.this);
					bb.add(comp);
					bb.open(0, 0);
				}
				
			});
			getParent().setMenu(m);
			m.setVisible(true);
		}
		return true;
	}




	@Override
	public void play(BasicPlayer player) throws BasicPlayerException {
		//先关闭上一次的 
		int status = player.getStatus();
		if(status == PLAYING || status == PAUSED){
			try { 
				player.stop();
			} catch (BasicPlayerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(null == realFile) {
			String url = info.getUrl();
			System.out.println("try load input stream ");
			SongLocation loc = SourceFactory.getSource(info.getSource()).getInputStream(url);
			if(null == loc) {
				System.out.println("retry load input stream!");
				loc = SourceFactory.getSource(info.getSource()).getInputStream(url);
			}
			if(null == loc || loc.length == 0) {
				System.out.println("input stream load failed!");
				throw new BasicPlayerException("can not download music");
			}
			System.out.println("input stream load success");
			Map<String, Object> property = new HashMap<String, Object>();
			property.put("songitem", this);
			property.put("duration", info.length);
			property.put("audio.length.bytes", loc.length);
			ICallback<Double> callBack = new ICallback<Double>() {

				@Override
				public Double callback(Double obj) {
					PlayUI.getInstance().setBuffered(obj);
					return obj;
				}
			};
			try {
				WriteOnReadInputStream input = new WriteOnReadInputStream(loc.input, loc.length, callBack) {

					@Override
					public void onDownloadEnd(File file) {
						realFile = file;
						TryListenItem.this.put("path", file.getAbsolutePath());
					}
					
				};
				player.open(input, property);
			} catch (IOException e) {
				throw new BasicPlayerException("invalid input", e);
			}
		} else {
			Map<String, Object> property = new HashMap<String, Object>();
			property.put("songitem", this);
			player.open(realFile, property);
		}
		
		player.play();
	}


	@Override
	public List<XRCLine> loadXrc(long allLength) {
		if(null == lrcs) {
			String lrcurl = info.getLrcUrl();
			String html = HTTPUtil.getInstance("player").getHtml(lrcurl, info.headers);
			lrcs = SourceFactory.getSource(info.getSource()).parse(html);
		}
		return lrcs;
	}

}
