package net.i2p.itoopie.gui;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.views.ChartPanel;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

import net.i2p.itoopie.gui.component.BandwidthChart;
import net.i2p.itoopie.gui.component.LogoPanel;
import net.i2p.itoopie.gui.component.ParticipatingTunnelsChart;
import net.i2p.itoopie.gui.component.RegisteredFrame;
import net.i2p.itoopie.gui.component.TabLogoPanel;
import net.i2p.itoopie.gui.component.util.TabChangeListener;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.util.IconLoader;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

public class Main {

	private static JFrame frame;
	private static JTabbedPane tabbedPane;
	private final static Color VERY_LIGHT = new Color(230,230,230);
	private final static Color LIGHT = new Color(215,215,215);
	private final static Color MEDIUM = new Color (175,175,175);
	private final static Color DARK = new Color(145,145,145);
	public final static int FRAME_WIDTH = 550;
	public final static int FRAME_HEIGHT = 400;

	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		GUIHelper.setDefaultStyle();


		frame = new RegisteredFrame("itoopie");
		frame.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		frame.setResizable(false);
		WindowHandler.registerMain(frame);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		TabLogoPanel overviewTab = new OverviewTab("itoopie-opaque12");
		tabbedPane.addTab(Transl._("Overview"), null, overviewTab, null);
		tabbedPane.addChangeListener(new TabChangeListener(overviewTab));
		
		
		TabLogoPanel configTab = new ConfigurationTab("itoopie-opaque12");
		tabbedPane.addTab(Transl._("Configuration"), null, configTab, null);
		tabbedPane.addChangeListener(new TabChangeListener(configTab));

		
		JPanel logPanel = new LogoPanel("itoopie-opaque12");
		tabbedPane.addTab(Transl._("Logs"), null, logPanel, null);
		
		
		TabLogoPanel aboutTab = new AboutTab("itoopie-opaque12");
		tabbedPane.addTab(Transl._("About"), null, aboutTab, null);

		
		JPanel statusPanel = new JPanel();
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setBounds(100, 15, 100, 100);
		statusPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel statusLbl = StatusHandler.getStatusLbl();
		statusLbl.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLbl, BorderLayout.CENTER);
		
		JPanel buttonWrapper = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonWrapper.getLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(0);
		statusPanel.add(buttonWrapper, BorderLayout.EAST);
		
		JButton settingsBtn = new JButton(Transl._("Settings"));
		buttonWrapper.add(settingsBtn);
		settingsBtn.setIcon(new ImageIcon(IconLoader.getIcon("cogwheel", 16)));
		settingsBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsFrame.start();
			}
		});
		
		frame.validate();
		frame.repaint(); // Force repaint to make sure that Logo is loaded.
		frame.setVisible(true);
	}
	
	
	/**
	 * Used to manually trigger updates for the tab being shown.
	 */
	public static void fireNewChange(){
		for (ChangeListener ch : tabbedPane.getChangeListeners()){
			ch.stateChanged(new ChangeEvent(tabbedPane));
		}
	}
	
}
