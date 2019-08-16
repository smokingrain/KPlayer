/**
 * @author xiaokui
 *
 *
 *
 *  时间：2014-11-4下午1:42:21
 */
package com.xk.player.lrc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.JWindow;

import com.xk.player.ui.PlayUI;




/**
 * @类名称：LyricFrame.java
 * @类描述：
 * @创建人：xiaokui
 * 时间：2014-11-4下午1:42:21
 */
public class LyricFrame extends JWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5530853132323975694L;
	private Integer startX=null;
	private Integer startY=null;
	private MyLyricPanel ly;
	
	/**
	 * 显示/隐藏桌面歌词
	 * @param bool
	 */
	public void hide(boolean bool) {
		setVisible(bool);
		if(bool) {
			setAlwaysOnTop(bool);
		}
		
	}
	
	/**
	 * 初始化桌面歌词
	 * @param ui
	 */
	public LyricFrame(PlayUI ui){
		setVisible(false);
		setBackground(new Color(0x00, 0x00, 0x00, 0));
		Dimension dis= Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(0, (int)(dis.height*0.65));
		this.setSize(dis.width, 150);
		ly=new MyLyricPanel(ui);
		ly.setBounds(0, 0, dis.width/2, 150);
		add(ly);
		setAlwaysOnTop(true);
		enableDrag();
	}
	
	
	/**
	 * 拖拽歌词
	 */
	private void enableDrag(){
		MouseListener ml=new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				startX = e.getX();
	            startY = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				startX = null;
	            startY = null;
			}
			
		};
		
		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if(null!=startX&&null!=startY){
					Point point=LyricFrame.this.getLocation();
					Point to=new Point();
					to.x=point.x+e.getX()-startX;
					to.y=point.y+e.getY()-startY;
					LyricFrame.this.setLocation(to);
				}
			}
			
		});
		addMouseListener(ml);
	}

	/**
	 * 设置歌词
	 * @param lines
	 */
	public void setLines(List<XRCLine> lines) {
		if(null!=ly){
			ly.setLines(lines);
		}
		
	}

	public MyLyricPanel getLy() {
		return ly;
	}

}
