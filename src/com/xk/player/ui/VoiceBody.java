package com.xk.player.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import com.xk.player.uilib.JBody;

public class VoiceBody implements JBody {

	@Override
	public void draw(GC gc,int pointX,int width,int height) {
		boolean adv = gc.getAdvanced();
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		Color back = gc.getBackground();
		Color fore = gc.getForeground();
		Color color = new Color(null, 255, 255, 255);
		gc.setBackground(color);
		gc.setForeground(color);
		int[]arr = new int[]{height / 2, height / 2 + 3, width - height / 2,height / 2,width - height / 2, height / 2 + 3};
		gc.fillPolygon(arr);
		gc.drawPolygon(arr);
		gc.setBackground(back);
		gc.setForeground(fore);
		gc.setAdvanced(adv);
		color.dispose();
	}

}
