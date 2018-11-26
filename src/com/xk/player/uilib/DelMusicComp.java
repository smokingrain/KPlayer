package com.xk.player.uilib;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.xk.player.ui.items.SongItem;
import com.xk.player.ui.items.TryListenItem;

public class DelMusicComp extends Composite implements ICallable<Boolean>{

	private ICallback<Boolean> callBack;
	private SongItem target;
	private Button chk;
	
	
	public DelMusicComp(Composite parent, int arg1) {
		super(parent, arg1);
		setBackgroundImage(parent.getParent().getBackgroundImage());
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		chk  = new Button(this, SWT.CHECK);
		chk.setBounds(66, 29, 109, 25);
		chk.setText("同时删除歌曲");
		
		
		Label label_1 = new Label(this, SWT.NONE);
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_1.setAlignment(SWT.CENTER);
		label_1.setBounds(61, 77, 61, 17);
		label_1.setText("确定");
		label_1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent mouseevent) {
				String file = target.getProperty().get("path");
				if(!(target instanceof TryListenItem)) {
					if(chk.getSelection()) {
						new File(file).delete();
					}
				}
				target.getParent().removeItem(target);
				callBack.callback(null);
			}
			
		});
		
		Label label_2 = new Label(this, SWT.NONE);
		label_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_2.setAlignment(SWT.CENTER);
		label_2.setBounds(152, 77, 61, 17);
		label_2.setText("取消");
		
		label_2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent mouseevent) {
				callBack.callback(null);
			}
		});
	}
	
	

	@Override
	public void setCallBack(ICallback<Boolean> callBack) {
		this.callBack = callBack;
		
	}

	public void setTarget(SongItem target) {
		this.target = target;
	}
	

}
