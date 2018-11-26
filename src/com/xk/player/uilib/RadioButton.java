package com.xk.player.uilib;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class RadioButton {

	private static final Integer MIN_WIDTH = 10;
	
	private Button btn;
	private CLabel lb;
	
	public RadioButton(Composite parent){
		btn = new Button(parent, SWT.RADIO);
		lb = new CLabel(parent, SWT.NONE);
		lb.setAlignment(SWT.CENTER);
		lb.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				if(!btn.getSelection()) {
					Control[] children = btn.getParent().getChildren();
					for(Control child : children) {
						if(child instanceof Button && (child.getStyle()|btn.getStyle()) == btn.getStyle() && child != btn) {
							Button btn = (Button) child;
							btn.setSelection(false);
						}
					}
					btn.setSelection(true);
					btn.update();
				}
			}
			
		});
	}
	
	public void setBounds(int x, int y, int width, int height) {
		if(width < MIN_WIDTH) {
			return;
		}
		btn.setBounds(x, y, MIN_WIDTH, height);
		lb.setBounds(x + MIN_WIDTH, y, width - MIN_WIDTH, height);
	}
	
	public void setText(String text) {
		lb.setText(text);
	}
	
	public String getText() {
		return lb.getText();
	}
	
	public void setForeground(Color foreground) {
		lb.setForeground(foreground);
	}
	
	public void setSelection(boolean select) {
		btn.setSelection(select);
	}
	
	public boolean getSelection() {
		return btn.getSelection();
	}
	
}
