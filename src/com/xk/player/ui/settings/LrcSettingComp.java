package com.xk.player.ui.settings;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;

import com.xk.player.tools.Config;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;

public class LrcSettingComp extends SettingParent {

	private Label bSelector;
	private Label lbColor;
	private Label ljColor;
	private Canvas proView;
	private Label lFontName;
	private Label lFontStyle;
	private Label bSelectorDesk;
	private Label lbColorDesk;
	private Label ljColorDesk;
	private Canvas preViewDesk;
	private Label lFontNameDesk;
	private Label lFontStyleDesk;
	
	public LrcSettingComp(Composite composite, int i) {
		super(composite, i);
		
		Group gNormal = new Group(this, SWT.NONE);
		gNormal.setText("经典歌词");
		gNormal.setBounds(10, 10, 303, 110);
		
		Label label = new Label(gNormal, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setBounds(10, 21, 61, 17);
		label.setText("歌词字体：");
		
		bSelector = new Label(gNormal, SWT.NONE);
		bSelector.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mouseevent) {
				if(mouseevent.button == 1) {
					selectFont(lFontName, lFontStyle, bSelector);
				}
			}
		});
		bSelector.setAlignment(SWT.CENTER);
		bSelector.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		bSelector.setBounds(243, 21, 50, 17);
		bSelector.setText("选择");
		
		Label label_1 = new Label(gNormal, SWT.NONE);
		label_1.setAlignment(SWT.RIGHT);
		label_1.setBounds(10, 44, 61, 17);
		label_1.setText("歌词颜色：");
		
		Label label_2 = new Label(gNormal, SWT.NONE);
		label_2.setAlignment(SWT.CENTER);
		label_2.setBounds(72, 44, 62, 17);
		label_2.setText("背景颜色");
		
		lbColor = new Label(gNormal, SWT.BORDER);
		lbColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent mouseevent) {
				if(mouseevent.button == 1) {
					selectColor(lbColor);
					proView.redraw();
				}
			}
		});
		lbColor.setBounds(140, 44, 24, 17);
		
		Label label_3 = new Label(gNormal, SWT.NONE);
		label_3.setAlignment(SWT.CENTER);
		label_3.setBounds(181, 44, 61, 17);
		label_3.setText("进度颜色");
		
		ljColor = new Label(gNormal, SWT.BORDER);
		ljColor.setBounds(243, 44, 24, 17);
		ljColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent mouseevent) {
				if(mouseevent.button == 1) {
					selectColor(ljColor);
					proView.redraw();
				}
			}
		});
		
		Label label_5 = new Label(gNormal, SWT.NONE);
		label_5.setAlignment(SWT.RIGHT);
		label_5.setBounds(10, 67, 61, 17);
		label_5.setText("预览：");
		
		proView = new Canvas(gNormal, SWT.NONE);
		proView.setBounds(82, 62, 211, 38);
		
		lFontName = new Label(gNormal, SWT.NONE);
		lFontName.setBounds(84, 21, 61, 17);
		
		lFontStyle = new Label(gNormal, SWT.NONE);
		lFontStyle.setBounds(160, 21, 61, 17);
		
		PreviewPaintListener listener = new PreviewPaintListener();
		listener.b = lbColor;
		listener.j = ljColor;
		listener.selector = bSelector;
		proView.addPaintListener(listener);
		
		Group group = new Group(this, SWT.NONE);
		group.setText("桌面歌词");
		group.setBounds(10, 126, 303, 110);
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setText("歌词字体：");
		label_4.setAlignment(SWT.RIGHT);
		label_4.setBounds(10, 21, 61, 17);
		
		bSelectorDesk = new Label(group, SWT.NONE);
		bSelectorDesk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mouseevent) {
				if(mouseevent.button == 1) {
					selectFont(lFontNameDesk, lFontStyleDesk, bSelectorDesk);
				}
			}
		});
		bSelectorDesk.setText("选择");
		bSelectorDesk.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		bSelectorDesk.setAlignment(SWT.CENTER);
		bSelectorDesk.setBounds(243, 21, 50, 17);
		
		Label label_7 = new Label(group, SWT.NONE);
		label_7.setText("歌词颜色：");
		label_7.setAlignment(SWT.RIGHT);
		label_7.setBounds(10, 44, 61, 17);
		
		Label label_8 = new Label(group, SWT.NONE);
		label_8.setText("背景颜色");
		label_8.setAlignment(SWT.CENTER);
		label_8.setBounds(72, 44, 62, 17);
		
		lbColorDesk = new Label(group, SWT.BORDER);
		lbColorDesk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent mouseevent) {
				selectColor(lbColorDesk);
				preViewDesk.redraw();
			}
		});
		lbColorDesk.setBounds(140, 44, 24, 17);
		
		Label label_10 = new Label(group, SWT.NONE);
		label_10.setText("进度颜色");
		label_10.setAlignment(SWT.CENTER);
		label_10.setBounds(181, 44, 61, 17);
		
		ljColorDesk = new Label(group, SWT.BORDER);
		ljColorDesk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent mouseevent) {
				selectColor(ljColorDesk);
				preViewDesk.redraw();
			}
		});
		ljColorDesk.setBounds(243, 44, 24, 17);
		
		Label label_12 = new Label(group, SWT.NONE);
		label_12.setText("预览：");
		label_12.setAlignment(SWT.RIGHT);
		label_12.setBounds(10, 67, 61, 17);
		
		preViewDesk = new Canvas(group, SWT.NONE);
		preViewDesk.setBounds(82, 62, 211, 38);
		
		lFontNameDesk = new Label(group, SWT.NONE);
		lFontNameDesk.setBounds(84, 21, 61, 17);
		
		lFontStyleDesk = new Label(group, SWT.NONE);
		lFontStyleDesk.setBounds(160, 21, 61, 17);
		
		PreviewPaintListener listenerDesk = new PreviewPaintListener();
		listenerDesk.b = lbColorDesk;
		listenerDesk.j = ljColorDesk;
		listenerDesk.selector = bSelectorDesk;
		preViewDesk.addPaintListener(listenerDesk);
	}

	private void selectColor(Label color) {
		ColorDialog cd = new ColorDialog(getShell(), SWT.NONE);
		cd.setRGB(color.getBackground().getRGB());
		RGB rgb = cd.open();
		if(null != rgb) {
			color.setBackground(new Color(null,rgb));
		}
	}
	
	private void selectFont(Label name, Label style, Label selector) {
		FontDialog fd = new FontDialog(getShell(), SWT.NONE);
		FontData fDate = fd.open();
		if(null != fDate) {
			selector.setData(fDate);
			name.setText(fDate.getName());
			
			switch(fDate.getStyle()) {
			case SWT.NORMAL:
				style.setText("常规");
				break;
			case SWT.BOLD:
				style.setText("粗体");
				break;
			case SWT.ITALIC:
				style.setText("斜体");
				break;
				default:break;
			}
		}
	}
	
	@Override
	public void setProperties(Config config) {
		Color back = lbColor.getBackground();
		config.br = back.getRed();
		config.bg = back.getGreen();
		config.bb = back.getBlue();
		Color proce = ljColor.getBackground();
		config.cr = proce.getRed();
		config.cg = proce.getGreen();
		config.cb = proce.getBlue();
		FontData fd = (FontData) bSelector.getData();
		config.fontName = fd.getName();
		config.fontStyle = fd.getStyle();
		Color backDesk = lbColorDesk.getBackground();
		config.dbr = backDesk.getRed();
		config.dbg = backDesk.getGreen();
		config.dbb = backDesk.getBlue();
		Color proceDesk = ljColorDesk.getBackground();
		config.dcr = proceDesk.getRed();
		config.dcg = proceDesk.getGreen();
		config.dcb = proceDesk.getBlue();
		FontData fdDesk = (FontData) bSelectorDesk.getData();
		config.dfontName = fdDesk.getName();
		config.dfontStyle = fdDesk.getStyle();
	}

	@Override
	public void loadValues(Config config) {
		Color back = new Color(null, config.br, config.bg, config.bb);
		Color proce = new Color(null, config.cr, config.cg, config.cb);
		lbColor.setBackground(back);
		ljColor.setBackground(proce);
		Font ft = new Font(null, config.fontName, 16, config.fontStyle);
		bSelector.setData(ft.getFontData()[0]);
		lFontName.setText(config.fontName);
		switch(config.fontStyle) {
		case SWT.NORMAL:
			lFontStyle.setText("常规");
			break;
		case SWT.BOLD:
			lFontStyle.setText("粗体");
			break;
		case SWT.ITALIC:
			lFontStyle.setText("斜体");
			break;
			default:break;
		}
		Color backDesk = new Color(null, config.dbr, config.dbg, config.dbb);
		lbColorDesk.setBackground(backDesk);
		Color proceDesk = new Color(null, config.dcr, config.dcg, config.dcb);
		ljColorDesk.setBackground(proceDesk);
		Font fontDesk = new Font(null, config.dfontName, 16, config.dfontStyle);
		bSelectorDesk.setData(fontDesk.getFontData()[0]);
		lFontNameDesk.setText(config.dfontName);
		switch(config.dfontStyle) {
		case SWT.NORMAL:
			lFontStyleDesk.setText("常规");
			break;
		case SWT.BOLD:
			lFontStyleDesk.setText("粗体");
			break;
		case SWT.ITALIC:
			lFontStyleDesk.setText("斜体");
			break;
			default:break;
		}
		
	}
	
	class PreviewPaintListener implements PaintListener {

		public Label b;
		public Label j;
		public Label selector;
		
		@Override
		public void paintControl(PaintEvent paintevent) {
			FontData fd = (FontData) selector.getData();
			if(null == fd) {
				return;
			}
			Font font = new Font(null, fd.getName(), 14, fd.getStyle());
			GC g = paintevent.gc;
			g.setAdvanced(true);
			g.setAntialias(SWT.ON);
			g.setFont(font);
			g.setForeground(b.getBackground());
			g.setBackground(b.getBackground());
			float[] dashList = new float[]{255,255};
			LineAttributes attributes =new LineAttributes(1, SWT.CAP_FLAT, SWT.JOIN_MITER, SWT.LINE_SOLID, dashList, 1, 3000);
			g.setLineAttributes(attributes);
			Pattern pattern =new Pattern(null, 95, 0, 10000, 0, b.getBackground(), 255, j.getBackground(), 255);
			g.setForegroundPattern(pattern);
			g.setBackgroundPattern(pattern);
			Path pt = new Path(null);
			String word = "战天音乐播放器歌词秀";
			pt.addString(word,2,5, font);
	        g.fillPath(pt);
	        g.drawPath(pt);
	        pt.dispose();
	        pattern.dispose();
	        font.dispose();
	        g.dispose();
			
		}
		
	}
}
