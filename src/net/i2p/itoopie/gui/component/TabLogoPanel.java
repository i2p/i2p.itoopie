package net.i2p.itoopie.gui.component;

import javax.swing.event.ChangeEvent;

public abstract class TabLogoPanel extends LogoPanel {
	
	private static final long serialVersionUID = 4526458659908868337L;

	public TabLogoPanel(String imageName) {
		super(imageName);
	}

	public abstract void onTabFocus(ChangeEvent e);
}
