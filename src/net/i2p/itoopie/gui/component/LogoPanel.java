package net.i2p.itoopie.gui.component;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.i2p.itoopie.util.IconLoader;

public class LogoPanel extends JPanel {
	private final static int IMAGE_SIZE = 128;
	private Image bg;

	/**
	 * An ordinary panel, but with a logotype added.
	 */
	private static final long serialVersionUID = 5619646652656877782L;

	public LogoPanel(final String imageName) {
		super();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				bg = IconLoader.getIcon(imageName, 128);
				LogoPanel.this.repaint();
				LogoPanel.this.updateUI();
				LogoPanel.this.validate();
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(bg, Math.abs(this.getWidth() - IMAGE_SIZE),
				Math.abs(this.getHeight() - IMAGE_SIZE), null);
	}
}
