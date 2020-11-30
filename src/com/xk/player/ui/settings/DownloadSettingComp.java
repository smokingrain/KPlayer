package com.xk.player.ui.settings;

import org.eclipse.swt.widgets.Composite;

import com.xk.player.tools.Config;
import com.xk.player.tools.SWTTools;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

public class DownloadSettingComp extends SettingParent {
	private Text lrcPath;
	private Text downPath;
	private Combo type;
	private Combo source;

	public DownloadSettingComp(Composite parent, int style) {
		super(parent, style);
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBounds(36, 24, 61, 17);
		lblNewLabel.setText("歌词目录:");
		
		lrcPath = new Text(this, SWT.BORDER);
		lrcPath.setEditable(false);
		lrcPath.setBounds(108, 21, 126, 23);
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.setBounds(239, 19, 55, 27);
		btnNewButton.setText("浏览");
		SWTTools.selectDir(lrcPath, btnNewButton);
		
		Label label = new Label(this, SWT.NONE);
		label.setText("下载目录:");
		label.setAlignment(SWT.RIGHT);
		label.setBounds(36, 77, 61, 17);
		
		downPath = new Text(this, SWT.BORDER);
		downPath.setEditable(false);
		downPath.setBounds(108, 69, 126, 23);
		
		Button button = new Button(this, SWT.NONE);
		button.setText("浏览");
		button.setBounds(239, 67, 55, 27);
		SWTTools.selectDir(downPath, button);
		
		
		Label label_1 = new Label(this, SWT.NONE);
		label_1.setText("下载类型:");
		label_1.setAlignment(SWT.RIGHT);
		label_1.setBounds(36, 133, 61, 17);
		
		type = new Combo(this, SWT.READ_ONLY);
		type.setBounds(108, 125, 126, 25);
		type.add("ape");
		type.add("mp3");
		
		Label sourceL = new Label(this, SWT.NONE);
		sourceL.setText("数据源:");
		sourceL.setAlignment(SWT.RIGHT);
		sourceL.setBounds(36, 189, 61, 17);
		
		source = new Combo(this, SWT.READ_ONLY);
		source.setBounds(108, 189, 126, 25);
		source.add("kugou");
		source.add("kuwo");
		source.add("ne");
		source.add("qier");
		source.add("migu");
	}
	

	@Override
	public void setProperties(Config config) {
		config.downloadPath = downPath.getText();
		config.lrcPath = lrcPath.getText();
		config.searchType = type.getItem(type.indexOf(type.getText()));
		config.downloadSource = source.getItem(source.indexOf(source.getText()));
	}

	@Override
	public void loadValues(Config config) {
		downPath.setText(config.downloadPath);
		lrcPath.setText(config.lrcPath);
		type.setText(config.searchType);
		source.setText(config.downloadSource);
	}
}
