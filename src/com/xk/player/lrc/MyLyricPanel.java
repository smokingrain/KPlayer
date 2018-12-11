/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xk.player.lrc;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

import com.xk.player.core.BasicController;
import com.xk.player.core.BasicPlayerEvent;
import com.xk.player.core.BasicPlayerListener;
import com.xk.player.tools.Config;
import com.xk.player.tools.FileUtils;
import com.xk.player.tools.JSONUtil;
import com.xk.player.tools.KrcText;
import com.xk.player.tools.LRCFactory;
import com.xk.player.tools.LrcParser;
import com.xk.player.tools.Util;
import com.xk.player.ui.PlayUI;
import com.xk.player.ui.items.SongItem;

/**
 *
 * @author xiaokui
 */
public class MyLyricPanel extends JPanel implements Runnable , BasicPlayerListener  {

	private int temp=-1;
	private List<XRCLine> lines;
	private List<Long>times=new ArrayList<>();
	private int cur=0;
	private boolean isUp=true;
	private boolean first=true;
    private static final long serialVersionUID = 20071214L;
    private long nowTime=0;
    private PlayUI ui;
    private ReentrantLock lock;
	private Condition cond;
	private Condition drawCond;
	private boolean paused=false;
	private boolean drawing =false;
	private Config config = Config.getInstance();
    
    
    public MyLyricPanel(PlayUI ui) {
    	this.ui=ui;
    	lock=new ReentrantLock();
		cond=lock.newCondition();
		drawCond=lock.newCondition();
        Thread th = new Thread(this);
        th.setDaemon(true);
        th.start();
        this.setDoubleBuffered(true);
    }

    public List<XRCLine> getLines() {
		return lines;
	}

    /**
     * 设置歌词数据
     * @param lines
     */
	public void setLines(List<XRCLine> lines) {
		if(drawing ){
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
		nowTime=0;
		cur=0;
		first=true;
		isUp=true;
		paused=false;
	}



	
	/**
	 * 绘制条件判断
	 */
    protected void paintComponent(Graphics g) {
    	temp*=-1;
    	setSize(getSize().width, getSize().height+temp);
        Graphics2D gd = (Graphics2D) g;
        if(null!=lines){
        	drawing=true;
        	update(gd);
        	drawing=false;
        	lock.lock();
			try {
				drawCond.signalAll();
			} finally {
				lock.unlock();
			}
        }
        gd.dispose();
    }

    
    private int findCur(long time){
		for(int i=times.size()-1;i>=0;i--){
			if(time>times.get(i)){
				return i;
			}
		}
		return 0;
	}
    
    /**
     * 绘制歌词
     * @param g
     */
    private void update(Graphics2D g){
    	if(nowTime==0 || null == lines || lines.isEmpty()){
			g.dispose();
			return;
		}
    	if(config.isDied()) {
			config = Config.getInstance();
		 }
    	int lastCur = cur;
    	long time=ui.getLrcOffset()+nowTime;//获取当前歌曲时间，加上前进后退值
    	cur=findCur(time);
    	if(cur != lastCur) {
    		isUp=!isUp;
    	}
    	Font ft=new Font(config.dfontName, config.dfontStyle, config.dfontSize);
    	g.setFont(ft);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);//抗锯齿
		XRCLine currentLine=lines.get(cur);
		if(currentLine.start==null||currentLine.length==null){
			g.dispose();
			return;
		}
		
        
        XRCLine other=null;//下一句
        if(cur==0){
			if(currentLine.nodes.size()>1){
				other=lines.get(cur+1);
			}
		}else if((double)(time-currentLine.start)/currentLine.length>0.01){
			if(cur+1<=lines.size()-1){
				other=lines.get(cur+1);
			}
		}else if((double)(time-currentLine.start)/currentLine.length<=0.01){
			other=lines.get(cur-1);
		}
        if(null!=other){//绘制下一句歌词
        	Graphics2D gc=(Graphics2D) g.create();
        	gc.setPaint(new Color(config.dbr, config.dbg, config.dbb));
        	FontMetrics fm=gc.getFontMetrics();
        	GlyphVector gv=ft.createGlyphVector(fm.getFontRenderContext(), other.getWord());
        	Shape shape=gv.getOutline();
        	if(isUp){
        		gc.translate(100, 100+fm.getAscent());
        	}else{
        		gc.translate(50, 50+fm.getAscent());
        	}
        	gc.fill(shape);
        }
        int now=0;
        for(int i=currentLine.nodes.size()-1;i>=0;i--){//获取当前这句歌词到第几个字
        	XRCNode node=currentLine.nodes.get(i);
        	if(time>currentLine.start+node.start){
        		now=i;
        		break;
        	}
        }
        float off=0;
        for(int i=0;i<now;i++){//获取当前字进度百分比
        	XRCNode node=currentLine.nodes.get(i);
        	off+=Util.getStringWidth(node.word, g);
        }
        if(currentLine.nodes.size()>0&&currentLine.start!=null&&currentLine.length!=null){
        	 XRCNode node=currentLine.nodes.get(now);
             float percent=(float)(time-(currentLine.start+node.start))/node.length;
             if(percent>1){
            	 percent=1;
             }
             off+=Util.getStringWidth(node.word,g)*percent;
             int baseLeft=isUp?50:100;
             if(off<=0){
            	 off=1;
             }
             
             g.setPaint(new LinearGradientPaint(baseLeft, 0f,(isUp?50:100)+off , 0f, new float[]{0.98f, 1f}, new Color[]{new Color(config.dcr, config.dcg, config.dcb), new Color(config.dbr, config.dbg, config.dbb)}));
             Util.drawString(g.create(), currentLine.getWord(), baseLeft,(isUp?50:100));
        }
        if(first){
        	first=false;
    	}
        
    }

    /**
     * 暂停
     * @param paused
     */
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


    public void run() {
        while (true) {
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
            try {
                Thread.sleep(100);
                if(getParent().isVisible()&&!paused&&null!=lines){
                	repaint();
                }
            } catch (Exception exe) {
                exe.printStackTrace();
            }
        }
    }

    @Override
	public void opened(Object stream, Map<String,Object> properties) {
    	nowTime=0;
		lines=null;
		cur=0;
		pause(false);
		Long allLength=(Long) properties.get("duration");
		SongItem item = (SongItem) properties.get("songitem");
		if(null != item){
			List<XRCLine> lines = item.loadXrc(allLength);
			if(null!=lines){
				setLines(lines);
			}
		}
	}

	@Override
	public void progress(int bytesread, long microseconds, byte[] pcmdata,
			Map<String,Object> properties) {
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
		// TODO Auto-generated method stub
		
	}

}
