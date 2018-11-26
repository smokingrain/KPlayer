/**
 * @author xiaokui
 * @版本 ：v1.0
 * @时间：2015-2-2下午4:05:20
 */
package com.xk.player.uilib;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xk.player.tools.Config;
import com.xk.player.tools.SourceFactory;
import com.xk.player.tools.sources.SongSeacher;


/**
 * @项目名称：TicketGetter
 * @类名称：AutoCombo.java
 * @类描述：
 * @创建人：xiaokui
 * 时间：2015-2-2下午4:05:20
 */
public abstract class AutoCombo{
	//不响应文字变化
	private boolean holding = false;
	//不响应提示
	private boolean sleeping = true;
	private boolean isActive=false;
	private boolean inited=false;
	private Shell shell;
	private StyledText combo;
	private List list;
	private Map<String,String> items = new HashMap<String,String>();
	private ExecutorService executor = Executors.newCachedThreadPool();
	public AutoCombo(StyledText combo){
		this.combo=combo;
		open();
	}
	public void init(){
		if(inited){
			return;
		}
		this.combo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				AutoCombo.this.focusGained();
			}
			@Override
			public void focusLost(FocusEvent e) {
				AutoCombo.this.focusLost();
			}
		});
		this.combo.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent verifyevent) {
				if(holding) {
					return ;
				}
				sleeping = false;
				String nowText = combo.getText();
				if(verifyevent.text.isEmpty()) {
					if(verifyevent.end - verifyevent.start == nowText.length()) {
						AutoCombo.this.focusLost();
						return ;
					}
					if(verifyevent.start == 0) {
						nowText = nowText.substring(verifyevent.end);
					}else {
						nowText = nowText.substring(0, verifyevent.start) + nowText.substring(verifyevent.end);
					}
					
				}else {
					nowText += verifyevent.text;
				}
				final String songName = nowText;
				final Long time = System.currentTimeMillis();
				executor.execute(new Runnable() {
					
					@Override
					public void run() {
						final Map<String, String> rst = SourceFactory.getSource(Config.getInstance().downloadSource).fastSearch(songName);//SongSeacher.fastSearch(songName);
						if(null == rst || rst.isEmpty()) {
							return;
						}
						Display.getDefault().asyncExec(new Runnable() {
							
							@Override
							public void run() {
								Long last = (Long) combo.getData("time");
								String curText = combo.getText().trim();
								if((null == last || last < time) && !curText.isEmpty()) {
									combo.setData("items", rst);
									combo.setData("time", time);
									AutoCombo.this.computeItens(songName);
								}
								
							}
						});
						
					}
				});
				
			}
		});
		inited=true;
	}
	
	public void open(Point loc,Point size){
		shell.setLocation(loc);
		shell.setSize(size);
		shell.open();
		shell.setVisible(true);
		OS.SetWindowPos(shell.handle , OS.HWND_TOPMOST, shell.getLocation().x , shell.getLocation().y , shell.getSize().x , shell.getSize().y , SWT.NULL);
	}
	
	private void open(){
		shell=new Shell(SWT.NO_TRIM|SWT.FILL);
		shell.setVisible(false);
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellDeactivated(ShellEvent e) {
//				shell.setVisible(false);
			}
			
		});
		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		list=new List(scrolledComposite,SWT.NONE);
		list.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				String key=list.getSelection()[0];
				String value=items.get(key);
				onSelect(key, value);
			}
			
		});
		StackLayout layout=new StackLayout();
		shell.setLayout(layout);
		layout.topControl=scrolledComposite;
		scrolledComposite.setContent(list);
		scrolledComposite.setMinSize(list.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	
	/**
	 *@author xiaokui
	 *@用途：
	 *@时间：2015-2-3下午6:06:01
	 */
	public void computeItens(String text) {
		items=(Map<String, String>) this.combo.getData("items");
		keyReleased(text);
	}
	private void focusLost(){
		if(!isActive&&OS.GetForegroundWindow()!=shell.handle){
			shell.setVisible(false);
		}
	}
	
	private void focusGained(){
		int length=this.combo.getText().length();
		this.combo.setSelection(new Point(length,length));
	}
	
	private void keyReleased(String text){
		if(sleeping) {
			return;
		}
		isActive=true;
		if(!shell.isVisible()){
			Point loc=this.combo.toDisplay(0, 0);
			Point csize=this.combo.getSize();
			loc.y=loc.y+csize.y;
			Point size=new Point(csize.x, 200);
			open(loc,size);
		}
		this.combo.setFocus();
		list.removeAll();
		Set<String> keys=items.keySet();
		for(String key:keys){
			if(key.startsWith(text)){
				list.add(key);
			}
		}
		if(list.getItemCount()>0){
			list.setSelection(0);
		}
		ScrolledComposite scrolledComposite=(ScrolledComposite) list.getParent();
		scrolledComposite.setMinSize(list.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		isActive=false;
	}
	
	public void sleep() {
		this.sleeping = true;
	}
	
	public void setHolding(boolean hold) {
		this.holding = hold;
	}
	
	public abstract void onSelect(String key,String value);
}
