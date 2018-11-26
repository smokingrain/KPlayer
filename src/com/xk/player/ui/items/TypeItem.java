package com.xk.player.ui.items;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import com.xk.player.uilib.ListItem;

public class TypeItem extends ListItem {

	private int height=62;
	private Image back;
	
	
	public TypeItem(Image back) {
		this.back=back;
	}
	
	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void draw(GC gc, int start, int width, int index) {
		if(selected){
			int alf=gc.getAlpha();
			gc.setAlpha(55);
			gc.fillRectangle(0, start, width, getHeight());
			gc.setAlpha(alf);
		}
		if(null!=back){
			gc.drawImage(back, (width-35)/2, start+(height-35)/2);
		}

	}

	@Override
	public boolean oncliek(MouseEvent e, int itemHeight,int index, int type) {
		return true;
	}

}
