package com.xk.player.uilib;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;


public class SearchComp extends Composite implements ICallable{
	private MyText text;
	private ICallback callback;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SearchComp(Composite parent, int style,String name) {
		super(parent, style);
		setBackgroundImage(parent.getParent().getBackgroundImage());
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		Label label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setBounds(36, 29, 59, 25);
		label.setText("歌名：");
		
		text = new MyText(this,SWT.BORDER|SWT.SINGLE);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		text.setBounds(98, 25, 139, 30);
		text.setNoTrim();
		text.setText(name);
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == SWT.CR) {
					String str=text.getText();
					if(!str.isEmpty()){
						callback.callback(str);
					}
				}
			}
			
		});
		
		Label label_1 = new Label(this, SWT.NONE);
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		label_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_1.setAlignment(SWT.CENTER);
		label_1.setBounds(61, 77, 61, 17);
		label_1.setText("搜索");
		label_1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent mouseevent) {
				String str=text.getText();
				if(!str.isEmpty()){
					callback.callback(str);
				}
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
				callback.callback(null);
			}
		});
	}

	@Override
	protected void checkSubclass() {
	}

	@Override
	public void setCallBack(ICallback callBack) {
		this.callback=callBack;
	}
}
