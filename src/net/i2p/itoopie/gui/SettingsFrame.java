package net.i2p.itoopie.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.component.RegisteredFrame;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import net.i2p.itoopie.security.ItoopieHostnameVerifier;

import javax.swing.BoxLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Component;

public class SettingsFrame extends RegisteredFrame{
	private static final Log _log = LogFactory.getLog(SettingsFrame.class);
	
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
					SettingsFrame window = new SettingsFrame();
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
	private SettingsFrame() {
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
			(new SettingsFrame()).setVisible(true);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		GUIHelper.setDefaultStyle();
		
		setTitle("itoopie Settings");
		setBounds(100, 100, 450, 150);
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

		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setAlignmentY(BOTTOM_ALIGNMENT);
		buttonPanel.setMaximumSize(new Dimension(2000, 24));
		getContentPane().add(buttonPanel);
		
		
		JButton btnDone = new JButton("Apply");
		buttonPanel.add(btnDone);
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
		
		
		JButton btnClose = new JButton(Transl._("Discard"));
		buttonPanel.add(btnClose);
		btnClose.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		
		// Run on init.
		populateSettings();
		
		validate();
	}
	
	@Override
	public void setVisible(boolean isVisible){
		if (isVisible){
			populateSettings();
		}
		super.setVisible(isVisible);
	}
	
	private void populateSettings(){
		textFieldRouterIP.setText(_conf.getConf("server.hostname", "127.0.0.1"));
		textFieldRouterPort.setText(_conf.getConf("server.port", 7650)+"");
		passwordField.setText(_conf.getConf("server.password", "itoopie"));
	}
	
	@SuppressWarnings("static-access")
	private int saveSettings(){
		ItoopieHostnameVerifier.clearRecentlyDenied();
		String oldIP = _conf.getConf("server.hostname", "127.0.0.1");
		int oldPort = _conf.getConf("server.port", 7650);
		String oldPW = _conf.getConf("server.password", "itoopie");

		
		String ipText = textFieldRouterIP.getText();
		try {
			InetAddress.getByName(ipText);
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
		
		String  portText = textFieldRouterPort.getText();
		int port = 0;
		try {
			port = Integer.parseInt(portText);
			if (port > 65535 || port <= 0)
				throw new NumberFormatException();
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
		
		String pwText = new String(passwordField.getPassword());
		try {
			_conf.setConf("server.hostname", ipText);
			_conf.setConf("server.port", port);
			_conf.setConf("server.password", pwText);
			JSONRPC2Interface.testSettings();
		} catch (InvalidPasswordException e) {
			_conf.setConf("server.password", oldPW);
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._("The password was not accepted as valid by the specified host.\r\n" + 
							"\r\n(by default the password is, \"itoopie\")"),
				    Transl._("Rejected password."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return SAVE_ERROR;
		} catch (JSONRPC2SessionException e) {
			_conf.setConf("server.hostname", oldIP);
			_conf.setConf("server.port", oldPort);
			_conf.setConf("server.password", oldPW);
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
		
		_conf.setConf("server.hostname", ipText);
		_conf.setConf("server.port", port);
		_conf.setConf("server.password", pwText);
		
		_log.debug("Ip old->new: \""+_conf.getConf("server.hostname","127.0.0.1")+"\"->\"" + ipText + "\"");
		_log.debug("Port old->new: \""+_conf.getConf("server.port",7650)+"\"->\"" + portText + "\"");
		_log.debug("Password old->new: \""+oldPW+"\"->\"" + pwText + "\"");
	
		StatusHandler.setStatus("Settings saved");
		
		(new Thread() {
			@Override
			public void run(){
				_conf.writeConfFile();
			}
		}).start();
		return SAVE_OK;
	}
	
}
