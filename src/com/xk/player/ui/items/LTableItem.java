package com.xk.player.ui.items;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;

import com.xk.player.tools.FileUtils;

import org.eclipse.wb.swt.SWTResourceManager;

import com.xk.player.tools.sources.IDownloadSource.SearchInfo;
import com.xk.player.uilib.ListItem;
import com.xk.player.uilib.MyList;

public abstract class LTableItem extends ListItem {
	protected boolean downloading=false;
	protected int persent=0; 
	protected SearchInfo info;
	protected boolean head=false;
	protected Image down;
	
	public LTableItem(SearchInfo info){
		this.info=info;
		down=SWTResourceManager.getImage(getClass(), "/images/download.png");
	}
	
	@Override
	public int getHeight() {
		return 30;
	}

	@Override
	public void draw(GC gc, int start, int width, int index) {
		if(getParent().getItemCount()-1>index){
			getParent();
			gc.drawLine(0, start+getHeight(), width-MyList.BAR_WIDTH, start+getHeight());
		}
		Font hqf=SWTResourceManager.getFont("宋体", 10, SWT.NORMAL);
		Color fo=gc.getForeground();
		if(selected||focused){
			int alf=gc.getAlpha();
			gc.setAlpha(55);
			gc.fillRectangle(0, start, width-MyList.BAR_WIDTH, getHeight());
			gc.setAlpha(alf);
			
			Color outer=SWTResourceManager.getColor(0XAF,0XEE,0XEE);
			gc.setForeground(outer);
		}
		Path hqp=new Path(null);
		hqp.addString(FileUtils.getLimitString(info.name, 12), 60, start+8, hqf);
		hqp.addString(FileUtils.getLimitString(info.album, 5), 240, start+8, hqf);
		hqp.addString(FileUtils.getLimitString(info.singer, 5), 330, start+8, hqf);
		if(!head){
			if(downloading){
				hqp.addString(persent+"%", 400, start+8, hqf);
			}else{
				gc.drawImage(down, 400, start);
			}
			hqp.addString((index-1)+"", 20, start+8, hqf);
			
		}else{
			hqp.addString("下载", 400, start+8, hqf);
			Color back=gc.getForeground();
			int line=gc.getLineWidth();
			gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			gc.setLineWidth(3);
			gc.drawLine(0, start+getHeight(), width, start+getHeight());
			gc.setForeground(back);
			gc.setLineWidth(line);
		}
		gc.fillPath(hqp);
		gc.drawPath(hqp);
		hqp.dispose();
		gc.setForeground(fo);
		
	}

	@Override
	public boolean oncliek(MouseEvent e, int itemHeight, int index, int type) {
		if(!head&&e.button==1&&e.x>400&&e.x<425){
			download();
		}
		return !head;
	}

	public boolean isHead() {
		return head;
	}

	public void setHead(boolean head) {
		this.head = head;
	}
	
	protected void flush(){
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				getParent().flush();
			}
		});
	}

	protected abstract void download();
	
}
