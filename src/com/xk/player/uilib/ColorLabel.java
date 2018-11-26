package com.xk.player.uilib;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ColorLabel extends Canvas {

	private Image inner;
	private Image focus;
	private Image now;
	private boolean changed=false;
	
	public ColorLabel(Composite arg0, int arg1,Image img,Image foc) {
		super(arg0, SWT.NONE);
		this.now=this.inner=img;
		this.focus=foc;
		setCursor(new Cursor(Display.getDefault(), SWT.CURSOR_HAND));
		addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent arg0) {
				GC gc=new GC(ColorLabel.this);
				boolean adv=gc.getAdvanced();
				gc.setAdvanced(true);
				gc.setAntialias(SWT.ON);
				gc.drawImage(now, 0, 0);
				gc.setAdvanced(adv);
				gc.dispose();
			}
		});
		addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent mouseevent) {
				if(changed){
					redraw();
					changed=false;
				}
				
			}
		});
		addMouseTrackListener(new MouseTrackAdapter() {
			
			@Override
			public void mouseExit(MouseEvent arg0) {
				now=inner;
				redraw();
			}
			
			@Override
			public void mouseEnter(MouseEvent arg0) {
				now=focus;
				redraw();
			}
		});
	}

	public void setInner(Image img){
		now=inner=img;
		changed=true;
	}
	
	public void setFocus(Image img){
		focus=img;
		changed=true;
	}
	
	
	@Override
	protected void checkSubclass() {
	}

}
