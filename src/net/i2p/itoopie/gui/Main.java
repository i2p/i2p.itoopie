package net.i2p.itoopie.gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.component.RegisteredFrame;
import net.i2p.itoopie.gui.component.TabLogoPanel;
import net.i2p.itoopie.gui.component.util.TabChangeListener;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.security.ItoopieHostnameVerifier;
import net.i2p.itoopie.util.IconLoader;

import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

public class Main {

	private JFrame frame;
	private JTabbedPane tabbedPane;
	private final WindowHandler windowHandler;
	private final ConfigurationManager _conf;
	public final static int FRAME_WIDTH = 550;
	public final static int FRAME_HEIGHT = 400;
	public final static int TABBED_PANE_HEIGHT = FRAME_HEIGHT - 66;

	/**
	 * Launch the application.
	 */
/*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
*/

	/**
	 * Create the application.
	 */
	public Main(WindowHandler wh, ConfigurationManager conf) {
		windowHandler = wh;
		_conf = conf;
	        HttpsURLConnection.setDefaultHostnameVerifier(new ItoopieHostnameVerifier(this, conf.getAppConfDir()));
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		GUIHelper.setDefaultStyle();
		//GUIHelper.setTabLooks();
		
		frame = new RegisteredFrame("itoopie", windowHandler);
		frame.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		frame.setResizable(false);
		frame.setBackground(GUIHelper.VERY_LIGHT);
		JRootPane root = frame.getRootPane();
		root.setLayout(null);
		//root.setBorder(BorderFactory.createLineBorder(GUIHelper.MEDIUM));
		
		windowHandler.registerMain(frame);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		root.add(tabbedPane);
		tabbedPane.setBounds(0, 0, FRAME_WIDTH-9, TABBED_PANE_HEIGHT);

		OverviewTab overviewTab = new OverviewTab("itoopie-opaque12", _conf);
		tabbedPane.addTab(' ' + Transl._t("Overview") + ' ', null, overviewTab, null);
		tabbedPane.addChangeListener(new TabChangeListener(overviewTab));
		
		
		TabLogoPanel configTab = new ConfigurationTab("itoopie-opaque12");
                ImageIcon icon = new ImageIcon(IconLoader.getIcon("cogwheel", 16));
		tabbedPane.addTab(' ' + Transl._t("I2P Control") + ' ', icon, configTab, null);
		tabbedPane.addChangeListener(new TabChangeListener(configTab));
		

		// pass overview tab to settingsframe to reset the charts on change
		TabLogoPanel settingsTab = new SettingsFrame("itoopie-opaque12", this, _conf, overviewTab);
		tabbedPane.addTab(' ' + Transl._t("Settings") + ' ', icon, settingsTab, null);
		tabbedPane.addChangeListener(new TabChangeListener(settingsTab));
		
		
		TabLogoPanel aboutTab = new AboutTab("itoopie-opaque12");
                icon = new ImageIcon(IconLoader.getIcon("itoopie-gray-opaque", 16));
		tabbedPane.addTab(' ' + Transl._t("About") + ' ', icon, aboutTab, null);

		
		JPanel statusPanel = new JPanel();
		root.add(statusPanel);
		statusPanel.setBounds(5, TABBED_PANE_HEIGHT - 3, FRAME_WIDTH-5, 35);
		statusPanel.setLayout(new BorderLayout(0, 0));
		statusPanel.setOpaque(false);
		
		JLabel statusLbl = StatusHandler.getStatusLbl();
		statusLbl.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLbl, BorderLayout.CENTER);
		
		JPanel buttonWrapper = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonWrapper.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(0);
		statusPanel.add(buttonWrapper, BorderLayout.EAST);
		buttonWrapper.setOpaque(false);
		
		frame.validate();
		frame.repaint(); // Force repaint to make sure that Logo is loaded.
		frame.setVisible(true);
	}
	
	
	/**
	 * Used to manually trigger updates for the tab being shown.
	 */
	public void fireNewChange() {
		for (ChangeListener ch : tabbedPane.getChangeListeners()){
			ch.stateChanged(new ChangeEvent(tabbedPane));
		}
	}
	
}
