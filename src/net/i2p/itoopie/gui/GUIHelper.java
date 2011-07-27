package net.i2p.itoopie.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class GUIHelper {
	private final static Color VERY_LIGHT = new Color(230,230,230);
	private final static Color LIGHT = new Color(215,215,215);
	private final static Color MEDIUM = new Color (175,175,175);
	private final static Color DARK = new Color(145,145,145);
	public final static FontUIResource DEFAULT_FONT = new FontUIResource(Font.SANS_SERIF,Font.PLAIN,12);
	
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
	    
	    setDefaultFonts();
	}
	
	private static void setDefaultFonts(){
		UIManager.put("Button.font", DEFAULT_FONT);
		UIManager.put("ToggleButton.font", DEFAULT_FONT);
		UIManager.put("RadioButton.font", DEFAULT_FONT);
		UIManager.put("CheckBox.font", DEFAULT_FONT);
		UIManager.put("ColorChooser.font", DEFAULT_FONT);
		UIManager.put("ComboBox.font", DEFAULT_FONT);
		UIManager.put("Label.font", DEFAULT_FONT);
		UIManager.put("List.font", DEFAULT_FONT);
		UIManager.put("MenuBar.font", DEFAULT_FONT);
		UIManager.put("MenuItem.font", DEFAULT_FONT);
		UIManager.put("RadioButtonMenuItem.font", DEFAULT_FONT);
		UIManager.put("CheckBoxMenuItem.font", DEFAULT_FONT);
		UIManager.put("Menu.font", DEFAULT_FONT);
		UIManager.put("PopupMenu.font", DEFAULT_FONT);
		UIManager.put("OptionPane.font", DEFAULT_FONT);
		UIManager.put("Panel.font", DEFAULT_FONT);
		UIManager.put("ProgressBar.font", DEFAULT_FONT);
		UIManager.put("ScrollPane.font", DEFAULT_FONT);
		UIManager.put("Viewport.font", DEFAULT_FONT);
		UIManager.put("TabbedPane.font", DEFAULT_FONT);
		UIManager.put("Table.font", DEFAULT_FONT);
		UIManager.put("TableHeader.font", DEFAULT_FONT);
		UIManager.put("TextField.font", DEFAULT_FONT);
		UIManager.put("PasswordField.font", DEFAULT_FONT);
		UIManager.put("TextArea.font", DEFAULT_FONT);
		UIManager.put("TextPane.font", DEFAULT_FONT);
		UIManager.put("EditorPane.font", DEFAULT_FONT);
		UIManager.put("TitledBorder.font", DEFAULT_FONT);
		UIManager.put("ToolBar.font", DEFAULT_FONT);
		UIManager.put("ToolTip.font", DEFAULT_FONT);
		UIManager.put("Tree.font", DEFAULT_FONT);
	}
}
