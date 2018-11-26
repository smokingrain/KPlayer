package com.xk.player.ui.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;


public abstract class SettingParent extends Composite implements Setable{

	public SettingParent(Composite composite, int i) {
		super(composite, i);
		setBackgroundMode(SWT.INHERIT_FORCE);
		setBackground(new Color(null, 0xf5, 0xff, 0xfa));
	}

	@Override
	protected void checkSubclass() {
	}

	
	
}
