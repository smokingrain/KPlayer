package com.xk.player.ui.settings;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.xk.player.tools.Config;
import com.xk.player.tools.SWTTools;
import com.xk.player.uilib.ICallable;
import com.xk.player.uilib.ICallback;
import com.xk.player.uilib.ListItem;
import com.xk.player.uilib.MyList;
import com.xk.player.uilib.listeners.ItemSelectionEvent;
import com.xk.player.uilib.listeners.ItemSelectionListener;

public class SettingComp extends Composite implements ICallable{

	private MyList left;
	private ICallback callback;
	private Config config;
	private Map<ListItem, SettingParent> comps = new HashMap<ListItem, SettingParent>();
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SettingComp(Composite parent, int style) {
		super(parent, style);
		config = Config.getInstance().clone();
		
		setBackgroundMode(SWT.INHERIT_FORCE);
		setBackgroundImage(parent.getParent().getBackgroundImage());
		
		Label textLabel = new Label(this, SWT.NONE);
		textLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		textLabel.setBackground(SWTResourceManager.getColor(0, 153, 255));
		textLabel.setBounds(0, 0, 450, 24);
		textLabel.setText("   基本设置");
		SWTTools.enableTrag(textLabel);
		
		left=new MyList(this, 100, 400);
		left.setBounds(10, 33, 100, 247);
		left.setSimpleSelect(true);
		left.setMask(55);
		
		
		Label okBtn = new Label(this, SWT.NONE);
		okBtn.setBackground(SWTResourceManager.getColor(0, 153, 255));
		okBtn.setAlignment(SWT.CENTER);
		okBtn.setBounds(280, 297, 61, 17);
		okBtn.setText("确定");
		okBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				if(null!=callback){
					for(SettingParent sp : comps.values()) {
						sp.setProperties(config);
					}
					Config.resetConfig(config);
					callback.callback(null);
				}
			}
		});
		
		Label noBtn = new Label(this, SWT.NONE);
		noBtn.setBackground(SWTResourceManager.getColor(0, 153, 255));
		noBtn.setAlignment(SWT.CENTER);
		noBtn.setBounds(365, 297, 61, 17);
		noBtn.setText("取消");
		noBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				if(null!=callback){
					callback.callback(null);
				}
			}
		});
		
		left.add(new ItemSelectionListener() {
			
			@Override
			public void selected(ItemSelectionEvent e) {
				ListItem itm = e.item;
				SettingParent sp = comps.get(itm);
				StackLayout sl = (StackLayout) sp.getParent().getLayout();
				if(null != sl.topControl) {
					sl.topControl.setVisible(false);
				}
				sl.topControl = sp;
				sl.topControl.setVisible(true);
			}
		});
		createComps();
	}

	/**
	 * 创建各个设置页面
	 * 用途：
	 * @date 2016年10月17日
	 */
	private void createComps() {
		
		Image down=SWTResourceManager.getImage(getClass(), "/images/download.png");
		SettingItem download=new SettingItem(down, "下载设置");
		left.addItem(download);
		
		Image img=SWTResourceManager.getImage(getClass(), "/images/lrcsetting.png");
		SettingItem song=new SettingItem(img, "歌词设置");
		left.addItem(song);
		
		Composite content = new Composite(this, SWT.NONE);
		content.setBounds(116, 33, 324, 247);
		StackLayout sl = new StackLayout();
		content.setLayout(sl);
		
		SettingParent lrcComp = new LrcSettingComp(content, SWT.NONE);
		lrcComp.setSize(content.getSize());
		sl.topControl = lrcComp;
		lrcComp.loadValues(config);
		comps.put(song, lrcComp);
		
		SettingParent downComp = new DownloadSettingComp(content, SWT.NONE);
		downComp.setSize(content.getSize());
		downComp.loadValues(config);
		comps.put(download, downComp);
		
		left.select(0, false);
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setCallBack(ICallback callBack) {
		this.callback=callBack;
		
	}
}
