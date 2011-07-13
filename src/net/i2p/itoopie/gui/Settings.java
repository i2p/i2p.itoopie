package net.i2p.itoopie.gui;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSlider;
import javax.swing.SwingWorker;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.component.ProgressiveDisclosurePanel;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import javax.swing.BoxLayout;
import java.awt.Component;

public class Settings extends RegisteredFrame{
	private final static int SAVE_OK = 0;
	private final static int SAVE_ERROR = 1;
	private static Boolean instanceShown = false;

	private JTextField textFieldRouterIP;
	private JTextField textFieldRouterPort;
	private JPasswordField passwordField;
	private ConfigurationManager _conf;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings window = new Settings();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private Settings() {
		_conf = ConfigurationManager.getInstance();
		initialize();
	}
	
	/**
	 * Dispose the JFrame and mark it as startable
	 */
	@Override
	public void dispose(){
		super.dispose();
		instanceShown = false;
	}
	
	/**
	 * Start settings windows if not already started.
	 */
	public static void start(){
		if (!instanceShown)
			(new Settings()).setVisible(true);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		GUIHelper.setDefaultStyle();
		
		setTitle("itoopie Settings");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel networkPanel = new JPanel();
		getContentPane().add(networkPanel);
		networkPanel.setLayout(null);
		
		JLabel lblI2PControl = new JLabel("Network:");
		lblI2PControl.setBounds(10, 10, 120, 15);
		networkPanel.add(lblI2PControl);
		lblI2PControl.setHorizontalAlignment(SwingConstants.RIGHT);
		
		
		JLabel lblRouterIP = new JLabel("IP address:");
		lblRouterIP.setBounds(138, 10, 100, 15);
		networkPanel.add(lblRouterIP);
		lblRouterIP.setHorizontalAlignment(SwingConstants.LEFT);
		
		textFieldRouterIP = new JTextField();
		textFieldRouterIP.setBounds(240, 10, 90, 19);
		networkPanel.add(textFieldRouterIP);
		textFieldRouterIP.setText("255.255.255");
		textFieldRouterIP.setColumns(10);
		
		
		JLabel lblRouterPort = new JLabel("Port:");
		lblRouterPort.setBounds(138, 35, 100, 15);
		networkPanel.add(lblRouterPort);
		lblRouterPort.setHorizontalAlignment(SwingConstants.LEFT);
		
		textFieldRouterPort = new JTextField();
		textFieldRouterPort.setBounds(240, 35, 45, 19);
		networkPanel.add(textFieldRouterPort);
		textFieldRouterPort.setText("65555");
		textFieldRouterPort.setColumns(10);
		
		
		JLabel lblRouterPassword = new JLabel("Password:");
		lblRouterPassword.setBounds(138, 60, 100, 15);
		networkPanel.add(lblRouterPassword);
		lblRouterPassword.setHorizontalAlignment(SwingConstants.LEFT);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(240, 60, 100, 19);
		networkPanel.add(passwordField);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(30, 85, 384, 1);
		networkPanel.add(separator);
		
		JButton btnDone = new JButton("Done");
		btnDone.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnDone.setAlignmentX(Component.RIGHT_ALIGNMENT);
		getContentPane().add(btnDone);
		btnDone.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (SAVE_OK == saveSettings()){
					dispose();					
				} else {
					populateSettings();
				}
			}
		});
		
		// Run on init.
		populateSettings();
	}
	
	private void populateSettings(){
		textFieldRouterIP.setText(_conf.getConf("server.hostname", "127.0.0.1"));
		textFieldRouterPort.setText(_conf.getConf("server.port", 7560)+"");
		passwordField.setText(_conf.getConf("server.password", "itoopie"));
	}
	
	@SuppressWarnings("static-access")
	private int saveSettings(){
		boolean newSettings = false;
		
		String ipText = textFieldRouterIP.getText();
		if (!ipText.equals(_conf.getConf("server.hostname", "127.0.0.1"))){
			System.out.println("Password changed: \""+_conf.getConf("server.hostname","127.0.0.1")+"\"->\"" + ipText + "\"");
			try {
				InetAddress.getByName(ipText);
				newSettings = true;
				_conf.setConf("server.hostname", ipText);
			} catch (UnknownHostException e) {
				JOptionPane.showConfirmDialog(
					    this,
					    Transl._(ipText + " can not be interpreted as an ip address.\r\n" + 
								"\r\nTry entering the ip address of the machine running i2p."),
					    Transl._("Invalid ip address."),
					    JOptionPane.DEFAULT_OPTION,
					    JOptionPane.ERROR_MESSAGE);
				return SAVE_ERROR;
			}
		}
		
		String  portText = textFieldRouterPort.getText();
		if (!portText.equals(_conf.getConf("server.port",7560)+"")){
			System.out.println("Password changed: \""+_conf.getConf("server.port",7560)+"\"->\"" + portText + "\"");
			try {
				int nbr = Integer.parseInt(portText);
				if (nbr > 65535 || nbr <= 0)
					throw new NumberFormatException();
				newSettings = true;
				_conf.setConf("server.port", nbr);
			} catch (NumberFormatException e){
				JOptionPane.showConfirmDialog(
					    this,
					    Transl._(portText + " can not be interpreted as a port.\r\n" + 
								"\r\nA port has to be a number in the range 1-65535."),
					    Transl._("Invalid port."),
					    JOptionPane.DEFAULT_OPTION,
					    JOptionPane.ERROR_MESSAGE);
				return SAVE_ERROR;
			}
		}
		
		String pwText = new String(passwordField.getPassword());
		String oldPW = _conf.getConf("server.password", "itoopie");
		if (!pwText.equals(oldPW)){
			try {
				System.out.println("Password changed: \""+oldPW+"\"->\"" + pwText + "\"");
				_conf.setConf("server.password", pwText);
				JSONRPC2Interface.testSettings();
				newSettings = true;
			} catch (InvalidPasswordException e) {
				_conf.setConf("server.password", oldPW);
				JOptionPane.showConfirmDialog(
					    this,
					    Transl._("The password was not accepted as valid by the specified host.\r\n" + 
								"\r\nPassword was reset to the default password, \"itoopie\"."),
					    Transl._("Rejected password."),
					    JOptionPane.DEFAULT_OPTION,
					    JOptionPane.ERROR_MESSAGE);
				return SAVE_ERROR;
			} catch (JSONRPC2SessionException e) {
				e.printStackTrace();
				JOptionPane.showConfirmDialog(
					    this,
					    Transl._("The remote host at the ip and port did not respond.\r\n" + 
								"\r\nMaybe I2PControl is not running on the remote I2P router, \r\n" + 
					    		"maybe the I2P router is not started."),
					    Transl._("Connection failed."),
					    JOptionPane.DEFAULT_OPTION,
					    JOptionPane.ERROR_MESSAGE);
				return SAVE_ERROR;
			}
		}
		if (newSettings){
			StatusHandler.setStatus("Settings saved");
			(new Thread() {
				@Override
				public void run(){
					_conf.writeConfFile();
				}
			}).start();
		}
		return SAVE_OK;
	}

}
