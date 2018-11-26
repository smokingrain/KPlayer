package com.xk.player.lrc;

import static com.xk.player.core.BasicPlayerEvent.EOM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.xk.player.core.BasicController;
import com.xk.player.core.BasicPlayer;
import com.xk.player.core.BasicPlayerEvent;
import com.xk.player.core.BasicPlayerException;
import com.xk.player.core.BasicPlayerListener;
import com.xk.player.tools.FileUtils;
import com.xk.player.tools.JSONUtil;
import com.xk.player.tools.LrcInfo;
import com.xk.player.tools.LrcParser;


/**
 * 歌词制作器
 * @author xiaokui
 *
 */
public class XLrcMaker implements BasicPlayerListener {
	private BasicPlayer player;
	protected Shell shell;
	private Text text;
	private int index = 0;
	private Button exit;
	private Button broser;
	private Text save;
	private boolean isStarted = false;
	private File fSave;
	private boolean canS = true;
	private int type = 1;
	private StringBuffer sb;
	private List<XRCLine> lines=new ArrayList<XRCLine>();
	private XRCLine cur;
	private XRCNode last;
	private long realTime = 0l;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new XLrcMaker("E:\\download\\陈奕迅 - 淘汰.mp3", 1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public XLrcMaker(String path, int type) {
		this.type = type;
		sb = new StringBuffer();
		open(path);
	}

	/**
	 * 读取已知歌词
	 * @param path
	 * @throws Exception
	 */
	private void prepare(String path) throws Exception {
		File songWord = new File(path.substring(0, path.lastIndexOf(".")) + ".zlrc");
		if(songWord.exists()){
			String data=FileUtils.readString(songWord.getAbsolutePath());
			List<XRCLine> olds=JSONUtil.toBean(data, JSONUtil.getCollectionType(List.class, XRCLine.class));
			showLrc(olds);
			return ;
		}
		songWord = new File(path.substring(0, path.lastIndexOf(".")) + ".lrc");
		LrcParser parser = new LrcParser(null);
		LrcInfo infos = parser.parser(new FileInputStream(songWord.getPath()));
		HashMap<Long, String> maps = infos.getInfos();
		showLrc(maps);

	}

	private long[] sortList(long[] timelist) {
		long t = 0;
		for (int i = 0; i < timelist.length; i++) {
			for (int j = i; j < timelist.length; j++) {
				if (timelist[i] > timelist[j]) {
					t = timelist[i];
					timelist[i] = timelist[j];
					timelist[j] = t;
				}
			}
		}
		return timelist;

	}

	private void showLrc(List<XRCLine> olds) {
		StringBuffer sb = new StringBuffer();
		for(XRCLine line:olds){
			sb.append(line.getWord() + "\n");
		}
		text.setText(sb.toString());
	}
	private void showLrc(HashMap<Long, String> maps) {
		long[] timelist = new long[maps.keySet().size()];
		Iterator<Long> itr = maps.keySet().iterator();
		int index = 0;
		while (itr.hasNext()) {
			timelist[index] = itr.next();
			index++;
		}
		timelist = sortList(timelist);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < timelist.length; i++) {
			String word = maps.get(timelist[i]);
			sb.append(word + "\n");
		}
		text.setText(sb.toString());

	}

	/**
	 * Open the window.
	 */
	public void open(String path) {
		Display display = Display.getDefault();
		createContents(path);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(final String path) {
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(XLrcMaker.class, "/images/jindutiao.png"));
		shell.setSize(803, 521);
		shell.setText("歌词制作器");
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (player != null) {
					try {
						player.stop();
						player.close();
					} catch (BasicPlayerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					player = null;
				}

			}

		});
		
		
		Label help = new Label(shell, SWT.NONE);
		help.setText("操作提示：按空格键跳到下一句或者下一个词。");
		help.setBounds(37, 10, 724, 25);

		text = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NORMAL));
		text.setBounds(37, 40, 724, 342);
		text.setTopIndex(0);
		text.forceFocus();
		text.setFocus();
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (isStarted) {
					switch (type) {
					case 1:
						switch (e.keyCode) {

						case 32:
							if(cur==null){
								cur=new XRCLine();
								lines.add(cur);
							}
							
							text.setSelection(index,++index);
							String select = text.getSelectionText();
							long time = realTime;
							char[] chars = select.toCharArray();
							if ("\n".equals(select)) {
								System.out.println("\\ nle");

							} else if (chars != null && chars.length > 0 && (int) chars[0] == 13) {
								cur.length=time-cur.start;
								last.length=cur.length-last.start;
								last=null;
								cur=new XRCLine();
								lines.add(cur);
								index++;
								text.setSelection(index,++index);
								select = text.getSelectionText();
								// System.out.println("kongge");
							} 
							if(null!=last){
								last.length=time-cur.start-last.start;
							}
							if(null==cur.start){
								cur.start=time;
							}
							last=new XRCNode();
							cur.nodes.add(last);
							last.start=time-cur.start;
							last.word=select;
							System.out.println(select);
							System.out.println(sb);
							break;
						default:
							break;
						}
						break;
					case 2:
						switch (e.keyCode) {
						case 32:
							text.setSelection(index, index = nextIndex(index) + 1);
							index++;
							String time = ((float)realTime)/1000f + "";
							String[] splied = time.split("\\.");
							int secends = Integer.parseInt(splied[0]);
							String melian;
							if (splied[1].length() == 1) {
								melian = splied[1].substring(0, 1) + "0";
							} else {
								melian = splied[1].substring(0, 2);
							}
							int secend = secends % 60;
							int min = secends / 60;
							sb.append("[" + (min >= 10 ? min : "0" + min) + ":" + (secend >= 10 ? secend : "0" + secend)
									+ "." + melian + "]" + text.getSelectionText());
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}

				}
			}

			private int nextIndex(int index) {
				String context = text.getText().trim();
				for (int i = index; i < context.length(); i++) {
					if ((int) (context.charAt(i)) == 13) {
						return i;
					}
				}
				return context.length();
			}

		});
		text.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				if (isStarted) {
					text.forceFocus();
					text.setFocus();
				}
			}

		});
		// text.setEditable(false);

		final Button start = new Button(shell, SWT.NONE);
		start.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (new File(save.getText()).isDirectory()) {
					canS = true;
					if (canS) {
						start.setEnabled(false);
						text.setEditable(false);
						isStarted = true;
						player = new BasicPlayer();
						player.addBasicPlayerListener(XLrcMaker.this);
						try {
							player.open(new File(path), new HashMap<String, Object>());
							player.play();
						} catch (BasicPlayerException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				} else {
					canS = false;
					MessageBox mb = new MessageBox(shell, SWT.NONE);
					mb.setText("提示");
					mb.setMessage("文件夹路径不合法，请重新设置！");
					mb.open();
				}
			}
		});
		start.setBounds(57, 399, 72, 22);
		start.setText("\u5F00\u59CB");

		final Button end = new Button(shell, SWT.NONE);
		end.setEnabled(false);
		end.setBounds(57, 429, 72, 22);
		end.setText("\u6B4C\u8BCD\u5199\u5B8C\u4E86");

		exit = new Button(shell, SWT.NONE);
		exit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		exit.setBounds(623, 429, 72, 22);
		exit.setText("\u9000\u51FA");

		broser = new Button(shell, SWT.NONE);
		broser.setBounds(488, 429, 72, 22);
		broser.setText("\u6D4F\u89C8\u6587\u4EF6\u5939");

		save = new Text(shell, SWT.BORDER);
		save.setBounds(162, 433, 304, 18);
		String savePath = path.substring(0, path.lastIndexOf("\\"));
		save.setText(savePath);
		String name = new File(path).getName().substring(0, new File(path).getName().lastIndexOf("."));
		if (new File(savePath).isDirectory()) {
			fSave = new File(savePath + "\\" + name + (type == 1 ? ".zlrc" : ".lrc"));
		} else {
			MessageBox mb = new MessageBox(shell, SWT.NONE);
			mb.setText("提示");
			mb.setMessage("文件夹路径不合法，请重新设置！");
			mb.open();
			canS = false;
		}

		Label label = new Label(shell, SWT.NONE);
		label.setBounds(162, 409, 72, 12);
		label.setText("\u6B4C\u8BCD\u53E6\u5B58\u4E3A");

		try {
			prepare(path);
		} catch (Exception e) {
			MessageBox mb = new MessageBox(shell, SWT.ERROR);
			mb.setText("警告");
			mb.setMessage("歌词不存在，您必须自己输入歌词！");
			mb.open();
			text.setEditable(true);
			start.setEnabled(false);
			end.setEnabled(true);
			end.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					text.setEditable(false);
					start.setEnabled(true);
					end.setEnabled(false);
				}
			});
		}

	}

	public void callEnd() {
		// 如果是媒体文件到达尾部事件
		if (player != null) {
			if (true) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						String xrc=JSONUtil.toJson(lines);
						sb.append(type == 1 ? xrc : "");
						if (fSave != null) {
							if (fSave.exists()) {
								if (!fSave.delete()) {
									MessageBox mb = new MessageBox(shell, SWT.ERROR);
									mb.setText("警告");
									mb.setMessage("文件被占用，请先解除占用！");
									mb.open();
									return;
								}

							}
							try {
								fSave.createNewFile();
							} catch (IOException e) {
								MessageBox mb = new MessageBox(shell, SWT.ERROR);
								mb.setText("警告");
								mb.setMessage("文件创建失败！");
								mb.open();
							}
						}
						BufferedWriter bw = null;
						try {
							FileOutputStream fout = new FileOutputStream(fSave);
							OutputStreamWriter osw = new OutputStreamWriter(fout, StandardCharsets.UTF_8);
							bw = new BufferedWriter(osw);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							bw.write(sb.toString());
							bw.close();
						} catch (Exception e) {

						}
						end();

					}

				});

			}
		}

	}

	protected void end() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageBox mb = new MessageBox(shell, SWT.NONE);
				mb.setText("提示");
				mb.setMessage("文件创建完毕！");
				mb.open();

			}

		});

	}

	@Override
	public void opened(Object stream, Map<String, Object> properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map<String, Object> properties) {
		realTime=microseconds/1000;
	}

	@Override
	public void stateUpdated(BasicPlayerEvent event) {
		if(event.getCode() == EOM){
			callEnd();
		}
		
	}

	@Override
	public void setController(BasicController controller) {
		// TODO Auto-generated method stub
		
	}
}
