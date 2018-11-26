package com.xk.player.ui.items;

import static com.xk.player.core.BasicPlayerEvent.PAUSED;
import static com.xk.player.core.BasicPlayerEvent.PLAYING;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;

import com.xk.player.core.BasicPlayer;
import com.xk.player.core.BasicPlayerException;
import com.xk.player.lrc.XLrcMaker;
import com.xk.player.lrc.XRCLine;
import com.xk.player.tools.FileUtils;

import org.eclipse.wb.swt.SWTResourceManager;
import org.jsoup.helper.StringUtil;

import com.xk.player.tools.Config;
import com.xk.player.tools.LRCFactory;
import com.xk.player.tools.SWTTools;
import com.xk.player.tools.sources.IDownloadSource.SearchInfo;
import com.xk.player.tools.SourceFactory;
import com.xk.player.ui.SendMusic;
import com.xk.player.uilib.BaseBox;
import com.xk.player.uilib.DelMusicComp;
import com.xk.player.uilib.ListItem;
import com.xk.player.uilib.MyList;
import com.xk.player.uilib.SearchComp;
import com.xk.player.uilib.SearchResultComp;

public class SongItem extends ListItem {

	private Map<String,String> property = new HashMap<String, String>();
	private int height=30;
	private int selectedHeight=50;
	private Image headDefault=SWTResourceManager.getImage(SongItem.class, "/images/head.png");
	private Image head;
	
	public SongItem(Map<String,String> property){
		this.property.putAll(property);
	}
	
	public void put(String key,String prop){
		property.put(key, prop);
	}
	
	@Override
	public int getHeight() {
		return selected?selectedHeight:height;
	}

	
	@Override
	public void unSelect() {
		if(null!=head){
			head.dispose();
			head=null;
		}
		super.unSelect();
	}

	@Override
	public void draw(GC gc, int start,int width,int index) {
		String pathPro = property.get("path");
		if(StringUtil.isBlank(pathPro)) {
			return;
		}
		String name=FileUtils.getLimitString(property.get("name"), 14);
		Font font=SWTResourceManager.getFont("黑体", 10, SWT.NORMAL);
		boolean hq = pathPro.endsWith(".ape");
		if(selected){
			int alf=gc.getAlpha();
			gc.setAlpha(55);
			gc.fillRectangle(0, start, width-MyList.BAR_WIDTH, getHeight());
			gc.setAlpha(alf);
			gc.drawImage((null==head||head.isDisposed())?headDefault:head, 15, start);
			Path path=new Path(null);
			path.addString(name, 15+58f, start+8, font);
			String all=property.get("all");
			if(null!=all){
				path.addString(all, width-MyList.BAR_WIDTH-40, start+30, font);
			}
			String now=property.get("now");
			if(null!=now){
				path.addString(now, 15+58f, start+30, font);
			}
			gc.drawPath(path);
			path.dispose();
		}else if(focused){
			int alf=gc.getAlpha();
			gc.setAlpha(55);
			gc.fillRectangle(0, start, width-MyList.BAR_WIDTH, getHeight());
			gc.setAlpha(alf);
			Path path=new Path(null);
			path.addString(index+"", 30f, start+8, font);
			path.addString(name, 50f, start+8, font);
			gc.drawPath(path);
			path.dispose();
		}else{
			Path path=new Path(null);
			path.addString(index+"", 30f, start+8, font);
			path.addString(name, 50f, start+8, font);
			gc.drawPath(path);
			path.dispose();
			
		}
		if(hq){
			Font hqf=SWTResourceManager.getFont("宋体", 10, SWT.NORMAL);
			Color bk=gc.getBackground();
			Color fo=gc.getForeground();
			Color inner=SWTResourceManager.getColor(0X66,0XCD,0XAA);
			Color outer=SWTResourceManager.getColor(0XAF,0XEE,0XEE);
			gc.setBackground(inner);
			gc.setForeground(outer);
			Path hqp=new Path(null);
			hqp.addString("HQ", width-MyList.BAR_WIDTH-40, start+8, hqf);
			gc.fillPath(hqp);
			gc.drawPath(hqp);
			hqp.dispose();
			gc.setBackground(bk);
			gc.setForeground(fo);
		}
	}

	@Override
	public boolean oncliek(MouseEvent e, int itemHeight,int index, int type) {
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
					getParent().select(SongItem.this,false);
				}
				
			});
			
			MenuItem createLrc = new MenuItem(m, SWT.CASCADE);
			createLrc.setText("制作歌词");
			
			Menu m11=new Menu(m);
			createLrc.setMenu(m11);
			
			MenuItem zlrc=new MenuItem(m11,SWT.NONE);
			zlrc.setText("制作zlrc歌词");
			zlrc.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e1) {
					new XLrcMaker(property.get("path"),1);
				}
			});
			
			MenuItem lrc=new MenuItem(m11,SWT.NONE);
			lrc.setText("制作lrc歌词");
			lrc.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e1) {
					new XLrcMaker(property.get("path"),2);
				}
			});
			
			
			
			MenuItem miSearch=new MenuItem(m, SWT.NONE);
			miSearch.setText("搜索歌词");
			miSearch.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					BaseBox bb=new BaseBox(getParent().getShell(), SWT.NO_TRIM);
					bb.getShell().setSize(300, 130);
					SWTTools.centerWindow(bb.getShell());
					SearchComp sc=new SearchComp(bb.getShell(), SWT.NONE,property.get("name"));
					bb.add(sc);
					Object result=bb.open(0, 0);
					if(null!=result){
						String name=result.toString();
						List<SearchInfo>lrcs = SourceFactory.getSource(Config.getInstance().downloadSource).getLrc(name);//SongSeacher.getLrcFromKuwo(name);
						BaseBox bbox=new BaseBox(getParent().getShell(), SWT.NO_TRIM);
						bbox.getShell().setSize(475,330);
						SWTTools.centerWindow(bbox.getShell());
						SearchResultComp src=new SearchResultComp(bbox.getShell(), SWT.NONE);
						src.setData(lrcs);
						src.setPath(property.get("name"));
						bbox.add(src);
						bbox.open(0, 0);
					}
				}
			});
			
			MenuItem mOpen = new MenuItem(m, SWT.NONE);
			mOpen.setText("打开文件所在文件夹");
			mOpen.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e1) {
					try {
						Runtime.getRuntime().exec(
								"rundll32 SHELL32.DLL,ShellExec_RunDLL "
										+ "Explorer.exe /select,"
										+ getProperty().get("path")
												);
					} catch (IOException e) {
						MessageBox mb = new MessageBox(getParent().getShell(), SWT.NONE);
						mb.setText("错误");
						mb.setMessage("文件不存在，建议删除歌曲。");
						mb.open();
					}
					
				}
			});
			
			MenuItem mSend = new MenuItem(m, SWT.NONE);
			mSend.setText("发送歌曲到手机");
			mSend.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e1) {
					File source = new File(getProperty().get("path"));
					BaseBox bbox = new BaseBox(getParent().getShell(), SWT.NO_TRIM);
					bbox.getShell().setSize(475,330);
					SWTTools.centerWindow(bbox.getShell());
					SendMusic sm = new SendMusic(bbox.getShell(), SWT.NONE);
					sm.setSource(source);
					bbox.add(sm);
					bbox.open(0, 0);
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
					comp.setTarget(SongItem.this);
					bb.add(comp);
					bb.open(0, 0);
				}
				
			});
			getParent().setMenu(m);
			m.setVisible(true);
		}
		return true;
	}


	public Map<String,String> getProperty() {
		return Collections.unmodifiableMap(property);
	}

	public Image getHead() {
		return head;
	}

	public void setHead(Image head) {
		this.head = head;
	}

	public void play(BasicPlayer player) throws BasicPlayerException {
		String path = getProperty().get("path");
		File file = new File(path);
		if(file.exists() && file.isFile()){
			int status = player.getStatus();
			if(status == PLAYING || status == PAUSED){
				try {
					player.stop();
				} catch (BasicPlayerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			Map<String, Object> property = new HashMap<String, Object>();
			property.put("songitem", this);
			player.open(file, property);
			player.play();
		}
	}
	
	public List<XRCLine> loadXrc(long allLength) {
		return LRCFactory.fromFile(property.get("name"), allLength);
	}
}
