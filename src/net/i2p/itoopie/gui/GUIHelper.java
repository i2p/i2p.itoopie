package net.i2p.itoopie.gui;

import java.awt.Color;

import javax.swing.UIManager;

public class GUIHelper {
	private final static Color VERY_LIGHT = new Color(230,230,230);
	private final static Color LIGHT = new Color(215,215,215);
	private final static Color MEDIUM = new Color (175,175,175);
	private final static Color DARK = new Color(145,145,145);
	
	public static void setDefaultStyle(){
		//Selected tab
		UIManager.put("TabbedPane.focus", VERY_LIGHT);
		UIManager.put("TabbedPane.selected", VERY_LIGHT);
		UIManager.put("TabbedPane.selectHighlight", Color.BLACK);
		//General shadow around each tab
		UIManager.put("TabbedPane.light", Color.WHITE);
		//Panel inside of tab
		UIManager.put("TabbedPane.contentAreaColor", VERY_LIGHT);
		//Button and unselected tab background
	    UIManager.put("Button.background", Color.WHITE); 
	}
}
