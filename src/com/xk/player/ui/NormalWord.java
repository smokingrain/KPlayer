package com.xk.player.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.sun.jna.platform.win32.WinUser.MSG;
import com.xk.hook.HotKeyListener;
import com.xk.hook.HotKeys;
import com.xk.player.core.BasicController;
import com.xk.player.core.BasicPlayerEvent;
import com.xk.player.core.BasicPlayerListener;
import com.xk.player.lrc.XRCLine;
import com.xk.player.lrc.XRCNode;
import com.xk.player.tools.Config;
import com.xk.player.tools.FileUtils;
import com.xk.player.tools.JSONUtil;
import com.xk.player.tools.KrcText;
import com.xk.player.tools.LRCFactory;
import com.xk.player.tools.LrcParser;
import com.xk.player.ui.items.SongItem;

import org.eclipse.wb.swt.SWTResourceManager;


public class NormalWord extends Canvas implements PaintListener,BasicPlayerListener,HotKeyListener{

	private List<XRCLine>lines;
	private List<Long>times=new ArrayList<>();
	private int cur=0;
	private int left=0;
	private ReentrantLock lock;
	private Condition cond;
	private Condition drawCond;
	private boolean paused=true;
	private long nowTime=0;
	private PlayUI ui;
	private boolean drawing=false;
	private String songName ="";
	private Config config = Config.getInstance();
	private Long allLength;
	
	
	public NormalWord(Composite parent,PlayUI ui) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.ui=ui;
		this.addPaintListener(this);
		lock=new ReentrantLock();
		cond=lock.newCondition();
		drawCond=lock.newCondition();
		backGround();
		HotKeys keys = HotKeys.getInstance();
		keys.add(this);
		keys.registerHotKey();
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				HotKeys keys = HotKeys.getInstance();
				keys.remove(this);
				keys.unregister();
			}
		});
	}
	
	private void backGround(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					if(paused){
						lock.lock();
						try {
							cond.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{
							lock.unlock();
						}
					}
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							if(!NormalWord.this.isDisposed()&&NormalWord.this.isVisible()){
								NormalWord.this.redraw();
							}
							
						}
					});
					try {
						Thread.sleep(70);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
		}).start();
		
	}
	
	private void pause(boolean paused){
		this.paused=paused;
		if(!paused){
			lock.lock();
			try {
				cond.signalAll();
			} finally{
				lock.unlock();
			}
			
		}
	}
	
	@Override
	public void paintControl(PaintEvent event) {
		if(nowTime==0){
			return;
		}
		GC g=event.gc;
		boolean adv=g.getAdvanced();
		g.setAdvanced(true);
		g.setAntialias(SWT.ON);
		if(null!=lines&&lines.size()>0&&!paused){
			drawing=true;
			drawXRC(g);
			drawing=false;
			lock.lock();
			try {
				drawCond.signalAll();
			} finally {
				lock.unlock();
			}
		}
		if(null==lines){
			drawSongName(g);
		}
		g.setAdvanced(adv);
		g.dispose();
	}

	private void drawSongName(GC g){
		if(config.isDied()) {
			config = Config.getInstance();
		}
		Font ft=SWTResourceManager.getFont( "楷体", config.fontSize, SWT.NORMAL);
		Point songWidth=g.stringExtent(songName);
        g.setFont(ft);
        g.setForeground(SWTResourceManager.getColor(config.br, config.bg, config.bb));
        g.setBackground(SWTResourceManager.getColor(config.br, config.bg, config.bb));
        Path path = new Path(null);
        path.addString(songName,left, getSize().y/2-songWidth.y/2, ft);
        g.drawPath(path);
        g.fillPath(path);
        path.dispose();
	}
	
	private int findCur(long time){
		for(int i=times.size()-1;i>=0;i--){
			if(time>times.get(i)){
				return i;
			}
		}
		return 0;
	}
	
	
	private void drawXRC(GC g){
		if(config.isDied()) {
			config = Config.getInstance();
		}
		long timeOffset=ui.getLrcOffset();
		long time=nowTime+timeOffset;
		cur=findCur(time);
		XRCLine currentLine=lines.get(cur);
		if(currentLine.start==null||currentLine.length==null){
//			cur++;
//			if(cur<lines.size()){
//				currentLine=lines.get(cur);
//			}else{
//				cur--;
//			}
			return;
		}
		float[] dashList = new float[]{255,255};
		Font ft = SWTResourceManager.getFont(config.fontName, config.fontSize, config.fontStyle);
        g.setFont(ft);
        g.setForeground(SWTResourceManager.getColor(config.br, config.bg, config.bb));
        g.setBackground(SWTResourceManager.getColor(config.br, config.bg, config.bb));
        float baseY=220;
        
        for(int i=1;i<=6;i++){//绘制前后语句
        	g.setAlpha(255-i*30);
        	Path path = new Path(null);
        	int temp=cur-i;
        	String str=temp<0?"":lines.get(temp).getWord();
        	path.addString(str,left, baseY-(i*30), ft);
        	temp=cur+i;
        	str=null;
        	str=temp>=lines.size()?"":lines.get(temp).getWord();
        	path.addString(str,left, baseY+(i*30), ft);
        	g.fillPath(path);
            path.dispose();
        }
        g.setAlpha(255);
        int now=0;
        for(int i=currentLine.nodes.size()-1;i>=0;i--){//获取当前字
        	XRCNode node=currentLine.nodes.get(i);
        	if(time>currentLine.start+node.start){
        		now=i;
        		break;
        	}
        }
        float off=0;
        for(int i=0;i<now;i++){//计算位移
        	XRCNode node=currentLine.nodes.get(i);
        	off+=g.stringExtent(node.word).x+1;
        }
        if(currentLine.nodes.size()>0&&currentLine.start!=null&&currentLine.length!=null){
        	 XRCNode node=currentLine.nodes.get(now);
             float percent=(float)(time-(currentLine.start+node.start))/node.length;
             if(percent>1){
            	 percent=1;
             }
             off+=g.stringExtent(node.word).x*percent;
             int baseLeft=left;
             if(off>420){
            	 baseLeft=(int) (left-(off-420));
            	 off=420f;
             }
     		 LineAttributes attributes =new LineAttributes(1, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_SOLID, dashList, 1, 3000);
     		 g.setLineAttributes(attributes);
             Pattern pattern =new Pattern(null, left+off, 0, 10000, 0, SWTResourceManager.getColor(config.br, config.bg, config.bb), 255, SWTResourceManager.getColor(config.cr, config.cg, config.cb), 255);
             g.setForegroundPattern(pattern);
             g.setBackgroundPattern(pattern);
             Path pt = new Path(null); 
             String wd=currentLine.getWord();
             pt.addString(wd,baseLeft,baseY, ft);
             g.fillPath(pt);
             g.drawPath(pt);
             pt.dispose();
             pattern.dispose();
        }
	}
	

	public synchronized void setLines(List<XRCLine> lines) {
		if(drawing){
			lock.lock();
			try {
				drawCond.await();
			} catch (InterruptedException e) {
			} finally {
				lock.unlock();
			}
		}
		this.lines = lines;
		times.clear();
		if(null!=lines){
			for(XRCLine line:lines){
				times.add(line.start);
			}
			Collections.sort(times);
		}
		cur=0;
	}

	public void loadLrc(SongItem item) {
		songName = item.getProperty().get("name");
		setLines(item.loadXrc(allLength));
	}
	
	@Override
	public void opened(Object stream, Map<String,Object> properties) {
		nowTime=0;
		setLines(null);
		cur=0;
		pause(false);
		allLength= ("Monkey's Audio (ape)".equals(properties.get("audio.type"))?(Long)properties.get("duration")*1000L:(Long)properties.get("duration"));
		SongItem item = (SongItem) properties.get("songitem");
		if(null != item){
			loadLrc(item);
		}
	}

	@Override
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map<String,Object> properties) {
		long now=ui.jumpedMillan+microseconds/1000;
		if(now-nowTime>70){
			nowTime=now;
		}
		
	}

	@Override
	public void stateUpdated(BasicPlayerEvent event) {
		if(event.getCode()==BasicPlayerEvent.PAUSED){
			pause(true);
		}else if(event.getCode()==BasicPlayerEvent.RESUMED){
			pause(false);
		}
	}

	@Override
	public void setController(BasicController controller) {
		
	}

	@Override
	public void notify(MSG msg) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				CutScreen cs = new CutScreen();
				cs.open();
			}
		});
	}
	
}
