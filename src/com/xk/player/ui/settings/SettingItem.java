package com.xk.player.ui.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;

import org.eclipse.wb.swt.SWTResourceManager;

import com.xk.player.uilib.ListItem;

public class SettingItem extends ListItem {

	private Image head;
	private String text="";
	@Override
	public int getHeight() {
		return 40;
	}
	
	public SettingItem(Image head,String text){
		this.head=head;
		this.text=text;
	}

	public void setHead(Image head){
		this.head=head;
	}
	
	public void setText(String text){
		this.text=text;
	}
	
	@Override
	public void draw(GC gc, int start, int width, int index) {
		Font font=SWTResourceManager.getFont("楷体", 12, SWT.NORMAL);
		if(selected){
			int alf=gc.getAlpha();
			gc.setAlpha(155);
			gc.fillRectangle(0, start, width, getHeight());
			gc.setAlpha(alf);
		}
		if(null!=head&&!head.isDisposed()){
			gc.drawImage(head, 0, start+10);
		}
		Path path=new Path(null);
		path.addString(text, 30f, start+14, font);
		gc.drawPath(path);
		gc.fillPath(path);
		path.dispose();
	}

	@Override
	public boolean oncliek(MouseEvent e, int itemHeight, int index, int type) {
		return true;
	}

}
