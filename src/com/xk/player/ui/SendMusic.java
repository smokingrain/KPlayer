package com.xk.player.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.wb.swt.SWTResourceManager;

import com.xk.player.net.MessageCallBack;
import com.xk.player.net.MessageListener;
import com.xk.player.net.MinaClient;
import com.xk.player.net.PackageInfo;
import com.xk.player.tools.ByteUtil;
import com.xk.player.tools.FileUtils;
import com.xk.player.tools.JSONUtil;
import com.xk.player.tools.SWTTools;
import com.xk.player.uilib.ColorLabel;
import com.xk.player.uilib.ICallable;
import com.xk.player.uilib.ICallback;
import com.xk.player.uilib.ListItem;
import com.xk.player.uilib.MyList;
import com.xk.player.uilib.listeners.ItemSelectionEvent;
import com.xk.player.uilib.listeners.ItemSelectionListener;

public class SendMusic extends Composite implements ICallable{
	private ICallback callBack;
	private Label label;
	private MyList list;
	private File source;
	private boolean sending = false;

	public SendMusic(Composite parent, int arg1) {
		super(parent, arg1);
		setBackgroundImage(parent.getParent().getBackgroundImage());
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		SWTTools.enableTrag(this);
		
		label = new Label(this, SWT.NONE);
		label.setBounds(10, 5, 374, 17);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setText("连接中的设备:");
		
		//关闭按钮
		Image clo=SWTResourceManager.getImage(PlayUI.class,"/images/close.png");
		Image clofoc=SWTResourceManager.getImage(PlayUI.class,"/images/close_focus.png");
		ColorLabel close = new ColorLabel(this, SWT.NONE,clo,clofoc);
		close.setBounds(442, 0, 28, 19);
		
		list = new MyList(this,455,260);
		list.setBounds(10, 30, 455, 260);
		list.setMask(180);
		list.setSimpleSelect(false);
		list.add(new ItemSelectionListener() {
			
			@Override
			public void selected(ItemSelectionEvent e) {
				if(sending) {
					return ;
				}
				sending = true;
				MinaClient client = MinaClient.getInstance();
				SendItem item = (SendItem) e.item;
				PackageInfo info = new PackageInfo(item.id, source.getName(), client.getCid(), MinaClient.MSG_ASK_SEND, MinaClient.APP);
				client.writeMessage(JSONUtil.toJson(info));
			}
		});
		
		close.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent mouseevent) {
				MinaClient.getInstance().close(false);
				callBack.callback(null);
			}
			
		});
		
		loadData();
	}

	public void setSource(File file) {
		this.source = file;
	}
	
	private void loadData() {
		MinaClient client = MinaClient.getInstance();
		MessageListener ml = new MessageListener();
		ml.registListener(new MsgHandler());
		client.setListener(ml);
		client.init("120.25.90.35", 5492);
		PackageInfo info = new PackageInfo(MinaClient.SERVER, "", client.getCid(), "hps", MinaClient.APP);
		client.writeMessage(JSONUtil.toJson(info));
	}
	
	@Override
	protected void checkSubclass() {
		
	}

	@Override
	public void setCallBack(ICallback callBack) {
		this.callBack=callBack;
		
	}

	class MsgHandler implements MessageCallBack {

		@Override
		public boolean callBack(PackageInfo info) {
			if(null == info) {
				return true;
			}
			if("hps".equals(info.getType())) {
				String msg = info.getMsg();
				List<Map<String,String>> rst = JSONUtil.toBean(msg, JSONUtil.getCollectionType(List.class, Map.class));
				for(Map<String, String> map : rst) {
					SendItem itm = new SendItem(map.get("name"), map.get("ip"), Long.parseLong(map.get("id")));
					list.addItem(itm);
				}
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						list.flush();
					}
				});
				
			}else if(MinaClient.RESULT_FILE_EXISTS.equals(info.getType())) {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						MessageBox mb = new MessageBox(getParent().getShell(), SWT.ERROR);
						mb.setMessage("文件已存在！");
						mb.setText("警告");
						mb.open();
					}
				});
			}else if(MinaClient.RESULT_NO_CLIENT.equals(info.getType())) {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						MessageBox mb = new MessageBox(getParent().getShell(), SWT.ERROR);
						mb.setMessage("APP已掉线！");
						mb.setText("警告");
						mb.open();
					}
				});
			}else if(MinaClient.RESULT_WORKING.equals(info.getType())) {
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						MessageBox mb = new MessageBox(getParent().getShell(), SWT.ERROR);
						mb.setMessage("目标正在进行传输！");
						mb.setText("警告");
						mb.open();
					}
				});
			}else if(MinaClient.RESULT_OK.equals(info.getType())) {
				MinaClient client = MinaClient.getInstance();
				PackageInfo pkt = new PackageInfo(info.getFrom(), "", client.getCid(), MinaClient.MSG_SEND_DATA, MinaClient.APP);
				try {
					FileInputStream in = new FileInputStream(source);
					byte[] data = new byte[128];
					while((in.read(data, 0, data.length)) >= 0) {
						String dataStr = ByteUtil.bytesToHexString(data);
						pkt.setMsg(dataStr);
						client.writeMessage(JSONUtil.toJson(pkt));
					}
					in.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pkt.setType(MinaClient.MSG_SEND_END);
				pkt.setMsg(MinaClient.MSG_SEND_END);
				client.writeMessage(JSONUtil.toJson(pkt));
			}else if(MinaClient.RESULT_OVER.equals(info.getType())) {
				sending =false;
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						MessageBox mb = new MessageBox(getParent().getShell(), SWT.NONE);
						mb.setMessage("传输完毕！");
						mb.setText("提示");
						mb.open();
					}
				});
			}
			return false;
		}
		
	}
	
	class SendItem extends ListItem {
		String name;
		String ip;
		Long id;
		
		SendItem(String name, String ip, Long id) {
			this.name = name;
			this.ip = ip;
			this.id = id;
		}
		
		@Override
		public int getHeight() {
			return 30;
		}

		@Override
		public void draw(GC gc, int start, int width, int index) {
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
			hqp.addString(FileUtils.getLimitString(name, 12), 60, start+8, hqf);
			hqp.addString(FileUtils.getLimitString(ip, 15), 240, start+8, hqf);
			hqp.addString(id + "", 330, start+8, hqf);
			gc.fillPath(hqp);
			gc.drawPath(hqp);
			hqp.dispose();
			gc.setForeground(fo);
			
		}

		@Override
		public boolean oncliek(MouseEvent e, int itemHeight, int index, int type) {
			return true;
		}
		
	}
	
}
