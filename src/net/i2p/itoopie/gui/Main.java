package net.i2p.itoopie.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

import net.i2p.itoopie.gui.component.LogoPanel;
import net.i2p.itoopie.util.IconLoader;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

public class Main {

	private JFrame frame;
	private final static Color VERY_LIGHT = new Color(230,230,230);
	private final static Color LIGHT = new Color(215,215,215);
	private final static Color MEDIUM = new Color (175,175,175);
	private final static Color DARK = new Color(145,145,145);

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


		frame = new RegisteredFrame();
		frame.setBounds(100, 100, 450, 300);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel overviewPanel = new LogoPanel("itoopie-opaque12");
		tabbedPane.addTab("Overview", null, overviewPanel, null);
		overviewPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel configPanel = new LogoPanel("itoopie-opaque12");
		tabbedPane.addTab("Configuration", null, configPanel, null);
		configPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel logPanel = new LogoPanel("itoopie-opaque12");
		tabbedPane.addTab("Logs", null, logPanel, null);
		logPanel.setLayout(new BorderLayout(0, 0));
		
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
		flowLayout.setVgap(3);
		statusPanel.add(buttonWrapper, BorderLayout.EAST);
		
		JButton settingsBtn = new JButton("Settings");
		buttonWrapper.add(settingsBtn);
		settingsBtn.setIcon(new ImageIcon(IconLoader.getIcon("cogwheel", 16)));
		settingsBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.start();
			}
		});
		
		frame.setVisible(true);
	}
}
