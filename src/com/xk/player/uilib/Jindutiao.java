package com.xk.player.uilib;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import com.xk.player.uilib.listeners.DragEvent;
import com.xk.player.uilib.listeners.DragListener;

public class Jindutiao extends Composite {

	private int height = 20;
	private List<DragListener> lissteners = new ArrayList<DragListener>();
	private int width;
	private double allLength;
	private double current = 0;
	private Canvas canvas;
	private STATE state = STATE.NORMAL;
	private int pointX = 0;//按钮绘制起点
	private double per = 0;
	private JBody body;
	private JBody button;
	private double subProgress = -1;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Jindutiao(Composite parent, int style,int width,double allLength) {
		super(parent, SWT.NONE);
		this.width = width;
		this.allLength = allLength;
		super.setSize(width, getHeight());
		setBackgroundMode(SWT.INHERIT_NONE);
		body = new DefaultBody();
		button = new DefaultButton();
		createBackground();
		createButton();
	}

	public void setAll(double all){
		if(all>0){
			this.allLength = all;
		}
	}
	
	public double getAll(){
		return allLength;
	}
	
	private void createBackground(){
		canvas = new Canvas(this, SWT.DOUBLE_BUFFERED);
		canvas.setBounds(0, 0, width, getHeight());
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setAdvanced(true);
				body.draw(gc, pointX, width, getHeight());
				button.draw(gc, pointX, width, getHeight());
				gc.dispose();
				
			}
		});
	}
	
	public void setCurrent(double current){
		if(current != this.current&&current >= 0&&current <= allLength){
			this.current = current;
			double per = (double)this.current / allLength;
			this.per = per;
			pointX = (int) ((width - getHeight()) * per);
			canvas.redraw();
		}
		
		
	}
	
	public double getCurrent(){
		return current;
	}
	
	public boolean setPersent(double per,boolean sync){
		if(sync && (STATE.DRAGING.equals(state))){
			return false;
		}
		if(per >=0 && per <= 1){
			double distance = Math.abs(per - this.per);
			if(sync && distance < 0.005){
				return false;
			}
			this.per = per;
			pointX = (int) ((width - getHeight()) * per);
			current = (int) (allLength * per);
			if(sync){
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						canvas.redraw();
					}
				});
			}else{
				canvas.redraw();
			}
			
			
		}
		return true;
	}
	
	public double getPersent(){
		return per;
	}
	
	private void createButton(){
		
		canvas.addMouseMoveListener(new MouseMoveListener() {//水平拖拽
			
			@Override
			public void mouseMove(MouseEvent mouseevent) {
				if(STATE.DRAGING.equals(state)){
					pointX = mouseevent.x < getHeight() / 2 ? 0 : (mouseevent.x > (width - getHeight() / 2) ? (width - getHeight()) : (mouseevent.x - getHeight() / 2));
					double per = (double)pointX / (width-getHeight());
					setPersent(per,false);
				}
				
			}
		});
		
		canvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				if(STATE.DRAGING.equals(state)){
					state = STATE.NORMAL;
					DragEvent event = new DragEvent();
					event.all = allLength;
					event.dragPoint = current;
					event.per = per;
					for(DragListener listener : lissteners){
						listener.dragEnd(event);
					}
				}
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				if(Math.abs(e.x - pointX - getHeight() / 2) < (getHeight()  /2)){
					state = STATE.DRAGING;
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent mouseevent) {
			}
		});
	}
	
	@Override
	public void setBounds(int i, int j, int k, int l) {
		super.setBounds(i, j, width, getHeight());
	}



	@Override
	public void setSize(int i, int j) {
		super.setSize(width, getHeight());
	}

	public boolean add(DragListener arg0) {
		return lissteners.add(arg0);
	}

	public boolean remove(Object arg0) {
		return lissteners.remove(arg0);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public JBody getBody() {
		return body;
	}

	public void setBody(JBody body) {
		this.body = body;
	}
	public JBody getButton() {
		return button;
	}

	public void setButton(JBody button) {
		this.button = button;
	}
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	public double getSubProgress() {
		return subProgress;
	}

	public void setSubProgress(Double subProgress) {
		this.subProgress = subProgress;
	}
	private enum STATE{
		NORMAL , DRAGING
	}
	
	class DefaultBody implements JBody{

		@Override
		public void draw(GC gc, int pointX, int width, int height) {
			Color back = gc.getForeground();
			Color color = new Color(null, 255, 255, 255);
			gc.setForeground(color);
			if(Jindutiao.this.subProgress > 0) {
				Rectangle rect = new Rectangle(height / 2, height / 2, width - height, 3);
				gc.drawRectangle(rect);
				int alpha = gc.getAlpha();
				int progress = (int) ((width - height) * Jindutiao.this.subProgress);
				if(progress - pointX > 0) {
					gc.setAlpha(150);
					gc.fillRectangle(pointX + height / 2, height / 2, progress - pointX, 3);
					gc.setForeground(color);
					gc.setAlpha(alpha);
				}
			} else {
				gc.drawLine(height/2, height/2+1, width-height/2, height/2+1);
			}
			gc.setLineWidth(3);
			gc.drawLine(height / 2, height / 2 + 1, pointX + height / 2, height / 2 + 1);
			color.dispose();
			gc.setForeground(back);
			back.dispose();
		}
	}
	
	class DefaultButton implements JBody{

		@Override
		public void draw(GC gc, int pointX, int width, int height) {
			Image img = SWTResourceManager.getImage(Jindutiao.class, "/images/jdt.png");
			gc.drawImage(img, pointX, 0);
		}
		
	}
}
