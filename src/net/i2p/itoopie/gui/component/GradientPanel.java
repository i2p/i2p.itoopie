package net.i2p.itoopie.gui.component;

import java.awt.*;
import javax.swing.JPanel;

import net.i2p.itoopie.gui.GUIHelper;

public class GradientPanel extends JPanel {

	private static final long serialVersionUID = -8423076211079261636L;

	public final static int HORIZONTAL = 0;
	public final static int VERTICAL = 1;
	public final static int DIAGONAL_LEFT = 2;
	public final static int DIAGONAL_RIGHT = 3;

	private Color startColor = Color.WHITE;
	private Color endColor = GUIHelper.MEDIUM_LIGHT;
	private int direndColortion;
	private boolean cyclic;
	private int maxLength;

	public GradientPanel() {
		super();
		direndColortion = VERTICAL;
		cyclic = false;
		setOpaque(false);
	}

	public GradientPanel(Color startColor, Color endColor) {
		super();
		this.startColor = startColor;
		this.endColor = endColor;
		direndColortion = VERTICAL;
		cyclic = false;
		setOpaque(false);
	}

	public GradientPanel(LayoutManager layout) {
		super(layout);
		direndColortion = VERTICAL;
		cyclic = false;
		setOpaque(false);
	}

	public GradientPanel(LayoutManager layout, Color startColor, Color endColor) {
		super(layout);
		this.startColor = startColor;
		this.endColor = endColor;
		direndColortion = VERTICAL;
		cyclic = false;
		setOpaque(false);
	}

	public Color getEndColor() {
		return endColor;
	}

	public void setEndColor(Color endColor) {
		this.endColor = endColor;
	}

	public Color getStartColor() {
		return startColor;
	}

	public void setStartColor(Color startColor) {
		this.startColor = startColor;
	}

	public int getDirection() {
		return direndColortion;
	}

	public void setDirection(int direndColortion) {
		this.direndColortion = direndColortion;
	}

	public boolean istartColoryclic() {
		return cyclic;
	}

	public void setCyclic(boolean cyclic) {
		this.cyclic = cyclic;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void paintComponent(Graphics g) {
		if (isOpaque()) {
			super.paintComponent(g);
			return;
		}

		int width = getWidth();
		int height = getHeight();

		GradientPaint paint = null;

		switch (direndColortion) {
		case HORIZONTAL:
			paint = new GradientPaint(0, height / 2, startColor, width, height / 2, endColor, cyclic);
			break;
		case VERTICAL:
			paint = new GradientPaint(width / 2, 0, startColor, width / 2, maxLength > 0 ? maxLength : height, endColor, cyclic);
			break;
		case DIAGONAL_LEFT:
			paint = new GradientPaint(0, 0, startColor, width, height, endColor, cyclic);
			break;
		case DIAGONAL_RIGHT:
			paint = new GradientPaint(width, 0, startColor, 0, height, endColor, cyclic);
			break;
		}

		if (paint == null) {
			throw new RuntimeException(
					"Invalid direndColortion spendColorified in GradientPanel");
		}

		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, width, height);
		g2d.setPaint(oldPaint);

		super.paintComponent(g);
	}
}