package net.i2p.itoopie.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.StatusHandler.DEFAULT_STATUS;
import net.i2p.itoopie.gui.component.GradientPanel;
import net.i2p.itoopie.gui.component.TabLogoPanel;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import net.i2p.itoopie.i2pcontrol.methods.I2PControl;
import net.i2p.itoopie.i2pcontrol.methods.I2PControl.ADDRESSES;
import net.i2p.itoopie.i2pcontrol.methods.I2PControl.I2P_CONTROL;
import net.i2p.itoopie.i2pcontrol.methods.GetI2PControl;
import net.i2p.itoopie.i2pcontrol.methods.SetI2PControl;
import net.i2p.itoopie.security.ItoopieHostnameVerifier;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * As of 0.0.4, this is a tab, not a popup frame.
 *
 */
public class SettingsFrame extends TabLogoPanel {
	
	private enum LOCAL_SAVE_STATUS {
		SAVE_OK,
		SAVE_ERROR,
		NO_CHANGES,
	};
	
	private static enum REMOTE_SAVE_STATUS{
		SAVE_FAILED_LOCALLY		{ public String toString(){return Transl._t("Settings aren't valid, not saving.");} },
		SAVE_FAILED_REMOTELY	{ public String toString(){return Transl._t("I2P router rejected settings.");} },
		SAVED_OK 				{ public String toString(){return Transl._t("Saved settings on I2P router.");} },
		SAVED_RESTART_NEEDED	{ public String toString(){return Transl._t("Saved settings on I2P router. I2P router needs to be restarted.");} },
		NO_CHANGES				{ public String toString(){return Transl._t("No changes found, settings not saved.");} }
	};
	



	private static final Log _log = LogFactory.getLog(SettingsFrame.class);
	private static Boolean instanceShown = false;

	// ConnectPanel
	private JTextField txtRouterIP;
	private JTextField txtRouterPort;
	private JPasswordField passwordField;
	// ChangePanel
/*
	private JComboBox comboAddress;
	private int currentComboAddressOption = 0;
	private JTextField txtNewPort;
	private JPasswordField txtNewPassword;
	private JPasswordField txtRetypeNewPassword;
*/
	
	private final ConfigurationManager _conf;
	private final OverviewTab _otab;
	private final Main _main;

	/**
	 * Create the application.
	 */
	public SettingsFrame(String imageName, Main m, OverviewTab otab) {
		super(imageName);
		setLayout(null);
		_conf = ConfigurationManager.getInstance();
		_otab = otab;
		_main = m;
		initialize();
	}
	
	@Override
	public void onTabFocus(ChangeEvent e) {
		populateSettings();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JPanel connectPanel = new JPanel();
		connectPanel.setOpaque(false);
		connectPanel.setLayout(null);
		connectPanel.setBounds(0, 40, 426, 99);
		add(connectPanel);
		setupConnectPanel(connectPanel);
		

/*
  This is confusing and probably isn't supported on the i2pcontrol side,
  certainly not with the webapp.

		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setBounds((96)/2, 108, (getWidth()-96), 2);
		add(separator);
		
		JPanel newChangePanel = new JPanel();
		newChangePanel.setLayout(null);
		newChangePanel.setOpaque(false);
		newChangePanel.setBounds(0, 110, 426, 135);
		add(newChangePanel);
		setupChangePanel(newChangePanel);
*/
		
		JButton btnDone = new JButton(Transl._t("Apply"));
		add(btnDone);
		btnDone.setBounds(412, 269, 122, 24);
		btnDone.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LOCAL_SAVE_STATUS localSettingStatus = saveRemoteHostSettings();
				if (LOCAL_SAVE_STATUS.SAVE_OK == localSettingStatus){

					StatusHandler.setStatus(Transl._t("Settings saved."));
				} else if (LOCAL_SAVE_STATUS.SAVE_ERROR == localSettingStatus){
					populateSettings();
				} else if (LOCAL_SAVE_STATUS.NO_CHANGES == localSettingStatus){
					StatusHandler.setStatus(Transl._t("Settings not saved, no changes found."));
				}
				
/*
				REMOTE_SAVE_STATUS newAddressStatus = saveNewAddress();
				switch (newAddressStatus){
				case SAVED_OK:
					StatusHandler.setStatus(Transl._t("New remote address") + ": " + REMOTE_SAVE_STATUS.SAVED_OK);
					break;
				case SAVE_FAILED_LOCALLY:
					StatusHandler.setStatus(Transl._t("New remote address") + ": " + REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY);
					break;
				case SAVE_FAILED_REMOTELY:
					StatusHandler.setStatus(Transl._t("New remote address") + ": " + REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY);
					break;
				}
				
				REMOTE_SAVE_STATUS newPortStatus = saveNewPort();
				switch (newPortStatus){
				case SAVED_OK:
					StatusHandler.setStatus(Transl._t("New remote port") + ": " + REMOTE_SAVE_STATUS.SAVED_OK);
					break;
				case SAVE_FAILED_LOCALLY:
					StatusHandler.setStatus(Transl._t("New remote port") + ": " + REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY);
					break;
				case SAVE_FAILED_REMOTELY:
					StatusHandler.setStatus(Transl._t("New remote port") + ": " + REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY);
					break;
				}
				
				REMOTE_SAVE_STATUS newPasswordStatus = saveNewPassword();
				switch (newPasswordStatus){
				case SAVED_OK:
					StatusHandler.setStatus(Transl._t("New remote password") + ": " + REMOTE_SAVE_STATUS.SAVED_OK);
					break;
				case SAVE_FAILED_LOCALLY:
					StatusHandler.setStatus(Transl._t("New remote password") + ": " + REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY);
					break;
				case SAVE_FAILED_REMOTELY:
					StatusHandler.setStatus(Transl._t("New remote password") + ": " + REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY);
					break;
				}
*/
				
				if (localSettingStatus != LOCAL_SAVE_STATUS.SAVE_ERROR
/*
				    &&
						newPortStatus != REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY && newPortStatus != REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY &&
						newPasswordStatus != REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY && newPasswordStatus != REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY &&
						newAddressStatus != REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY && newAddressStatus != REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY
*/
				    ) {
					_main.fireNewChange();
				}
			}
		});
		
		validate();
	}
	
	private void setupConnectPanel(JPanel networkPanel){
		JLabel lblI2PControl = new JLabel(Transl._t("Connect to I2P node"));
		lblI2PControl.setBounds(10, 10, 228, 15);
		networkPanel.add(lblI2PControl);
		lblI2PControl.setHorizontalAlignment(SwingConstants.RIGHT);
		
		
		JLabel lblRouterIP = new JLabel(Transl._t("IP address:"));
		lblRouterIP.setBounds(138, 35, 100, 15);
		networkPanel.add(lblRouterIP);
		lblRouterIP.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtRouterIP = new JTextField();
		txtRouterIP.setBounds(243, 35, 90, 19);
		networkPanel.add(txtRouterIP);
		
		
		JLabel lblRouterPort = new JLabel(Transl._t("Port:"));
		lblRouterPort.setBounds(10, 60, 228, 15);
		networkPanel.add(lblRouterPort);
		lblRouterPort.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtRouterPort = new JTextField();
		txtRouterPort.setBounds(243, 60, 90, 19);
		networkPanel.add(txtRouterPort);
		
		
		JLabel lblRouterPassword = new JLabel(Transl._t("Password:"));
		lblRouterPassword.setBounds(10, 85, 228, 15);
		networkPanel.add(lblRouterPassword);
		lblRouterPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(243, 85, 90, 19);
		networkPanel.add(passwordField);
	}

	
/*
  See comments above

	private void setupChangePanel(JPanel changePanel){
		JLabel lblChange = new JLabel(Transl._t("Change I2PControl"));
		lblChange.setBounds(10, 10, 228, 15);
		changePanel.add(lblChange);
		lblChange.setHorizontalAlignment(SwingConstants.RIGHT);
		
		
		JLabel lblAddress = new JLabel(Transl._t("Change address:"));
		lblAddress.setBounds(10, 30, 228, 15);
		changePanel.add(lblAddress);
		lblAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		
		comboAddress = new JComboBox();
		setupAddressComboBox(comboAddress);
		comboAddress.setBounds(240, 30, 170, 19);
		changePanel.add(comboAddress);
		
		JLabel lblPort = new JLabel(Transl._t("Change port:"));
		lblPort.setBounds(10, 60, 228, 15);
		changePanel.add(lblPort);
		lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtNewPort = new JTextField();
		txtNewPort.setBounds(240, 60, 90, 19);
		txtNewPort.setHorizontalAlignment(SwingConstants.RIGHT);
		changePanel.add(txtNewPort);
		
		
		JLabel lblNewPassword = new JLabel(Transl._t("New password:"));
		lblNewPassword.setBounds(10, 90, 228, 15);
		changePanel.add(lblNewPassword);
		lblNewPassword.setHorizontalAlignment(SwingConstants.RIGHT);

		
		txtNewPassword = new JPasswordField();
		txtNewPassword.setBounds(240, 90, 90, 19);
		changePanel.add(txtNewPassword);
		
		
		JLabel lblRetypeNewPassword = new JLabel(Transl._t("Repeat password:"));
		lblRetypeNewPassword.setBounds(10, 115, 228, 15);
		changePanel.add(lblRetypeNewPassword);
		lblRetypeNewPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txtRetypeNewPassword = new JPasswordField();
		txtRetypeNewPassword.setBounds(240, 115, 90, 19);
		changePanel.add(txtRetypeNewPassword);
	}

	
	private void setupAddressComboBox(JComboBox comboBox) {
		for (ADDRESSES addr : ADDRESSES.values()){
			comboBox.addItem(addr.toString());
		}
		comboBox.setSelectedIndex(0);
	}
*/

	private void populateSettings(){
		txtRouterIP.setText(_conf.getConf("server.hostname", "127.0.0.1"));
		txtRouterPort.setText(_conf.getConf("server.port", 7650)+"");
		passwordField.setText(_conf.getConf("server.password", "itoopie"));

		this.getRootPane().repaint(); // Repainting jlabel or jpanel is not enough.
		
/*
		(new Thread(){
			@Override
			public void run(){
				try {
					EnumMap<I2P_CONTROL, Object> em = GetI2PControl.execute(I2P_CONTROL.ADDRESS);
					String currentAddress = (String) em.get(I2P_CONTROL.ADDRESS);
					final ADDRESSES currentAddr = I2PControl.getAddressEnum(currentAddress);

					if (currentAddr != null){
						currentComboAddressOption = currentAddr.ordinal();
					
						SwingUtilities.invokeLater(new Runnable(){
							@Override
							public void run(){
								comboAddress.setSelectedIndex(currentAddr.ordinal());			
							}
						});
					}
					
				} catch (InvalidPasswordException e) {
				} catch (JSONRPC2SessionException e) {
				}
				
			}
		}).start();
*/
	}
	
	@SuppressWarnings("static-access")
	private LOCAL_SAVE_STATUS saveRemoteHostSettings(){
		ItoopieHostnameVerifier.clearRecentlyDenied();
		String oldIP = _conf.getConf("server.hostname", "127.0.0.1");
		int oldPort = _conf.getConf("server.port", 7650);
		String oldPW = _conf.getConf("server.password", "itoopie");
		
		String ipText = txtRouterIP.getText();
		String portText = txtRouterPort.getText();
		String pwText = new String(passwordField.getPassword());
		
//		// Exit SAVE_STATUS.NO_CHANGES if no changes are found. Possibly just an annoying check.
//		if (ipText.equals(oldIP) && portText.equals(oldPort+"") && pwText.equals(oldPW))
//			return LOCAL_SAVE_STATUS.NO_CHANGES;
		
		try {
			InetAddress.getByName(ipText);
		} catch (UnknownHostException e) {
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._t("\"{0}\" cannot be interpreted as an ip address.", ipText) + "\n\n" + 
							Transl._t("Try entering the ip address of the machine running i2p."),
				    Transl._t("Invalid ip address."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return LOCAL_SAVE_STATUS.SAVE_ERROR;
		}
		
		int port = 0;
		try {
			port = Integer.parseInt(portText);
			if (port > 65535 || port <= 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._t("\"{0}\" cannot be interpreted as a port.", portText) + "\n\n" + 
							Transl._t("Port must be a number in the range 1-65535."),
				    Transl._t("Invalid port."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return LOCAL_SAVE_STATUS.SAVE_ERROR;
		}
		
		try {
			_conf.setConf("server.hostname", ipText);
			_conf.setConf("server.port", port);
			_conf.setConf("server.password", pwText);
			JSONRPC2Interface.testSettings();
			if (!oldIP.equals(ipText) || oldPort != port)
				_otab.clearGraphs();
		} catch (InvalidPasswordException e) {
			_conf.setConf("server.password", oldPW);
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._t("The password was not accepted as valid by the specified host.\n" + 
							"\n(the default password is \"itoopie\")"),
				    Transl._t("Rejected password."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return LOCAL_SAVE_STATUS.SAVE_ERROR;
		} catch (JSONRPC2SessionException e) {
			_conf.setConf("server.hostname", oldIP);
			_conf.setConf("server.port", oldPort);
			_conf.setConf("server.password", oldPW);
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._t("The remote host at the ip and port did not respond.\n" + 
							"\nMaybe I2PControl is not running on the remote I2P router, \n" + 
				    		"maybe the I2P router is not started."),
				    Transl._t("Connection failed."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return LOCAL_SAVE_STATUS.SAVE_ERROR;
		}
		
		_conf.setConf("server.hostname", ipText);
		_conf.setConf("server.port", port);
		_conf.setConf("server.password", pwText);
		
	
		
		(new Thread() {
			@Override
			public void run(){
				_conf.writeConfFile();
			}
		}).start();
		return LOCAL_SAVE_STATUS.SAVE_OK;
	}
	
/*
	private REMOTE_SAVE_STATUS saveNewPassword(){
		String oldPW = _conf.getConf("server.password", "itoopie");
		String pw = new String(txtNewPassword.getPassword());
		String pwRepeat = new String(txtRetypeNewPassword.getPassword());
		
		if ((pw.length() < 1) && (pwRepeat.length() < 1)){
			return REMOTE_SAVE_STATUS.NO_CHANGES;
		}
		
		if (!pw.equals(pwRepeat)){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._t("The new password and the repeated new password do not match.") + "\n",
				    Transl._t("Mistyped password."),
				    JOptionPane.OK_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY;
		}
		
		if (pw.equals(oldPW)){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._t("The new password is the same as the old password.") + "\n",
				    Transl._t("No new password."),
				    JOptionPane.OK_CANCEL_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return REMOTE_SAVE_STATUS.NO_CHANGES;
		}
		
		HashMap<I2P_CONTROL, String> hm = new HashMap<I2P_CONTROL, String>();
		hm.put(I2P_CONTROL.PASSWORD, pw);
		try {
			SetI2PControl.execute(hm);
			_conf.setConf("server.password", pw); // Reflect remote changes in local settings.
			passwordField.setText(pw);
			return REMOTE_SAVE_STATUS.SAVED_OK;
		} catch (InvalidPasswordException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.INVALID_PASSWORD);
			return REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY;
		} catch (JSONRPC2SessionException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.NOT_CONNECTED);
			return REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY;
		} catch (InvalidParametersException e) {
			return REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY;
		}
	}
	
	
	private REMOTE_SAVE_STATUS saveNewPort(){
		String oldPort = _conf.getConf("server.port", 7650) + "";
		String port = txtNewPort.getText();
		
		if (port.length() < 1){
			return REMOTE_SAVE_STATUS.NO_CHANGES;
		}
		
		if (port.equals(oldPort)){
			return REMOTE_SAVE_STATUS.NO_CHANGES;
		}
		
		int nbrPort = 0;
		try {
			nbrPort = Integer.parseInt(port);
			if (nbrPort > 65535 || nbrPort < 1)
				throw new NumberFormatException();
		} catch (NumberFormatException e){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._t("\"{0}\" cannot be interpreted as a port.", port) + "\n\n" + 
							Transl._t("Port must be a number in the range 1-65535."),
				    Transl._t("Invalid port."),
				    JOptionPane.OK_CANCEL_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY;
		}
		
		HashMap<I2P_CONTROL, String> hm = new HashMap<I2P_CONTROL, String>();
		hm.put(I2P_CONTROL.PORT, nbrPort + "");
		try {
			SetI2PControl.execute(hm);
			_conf.setConf("server.port", nbrPort); // Reflect remote changes in local settings.
			txtRouterPort.setText(port+"");
			return REMOTE_SAVE_STATUS.SAVED_OK;
		} catch (InvalidPasswordException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.INVALID_PASSWORD);
			return REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY;
		} catch (JSONRPC2SessionException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.NOT_CONNECTED);
			return REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY;
		} catch (InvalidParametersException e) {
			return REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY;
		}
	}
	
	private REMOTE_SAVE_STATUS saveNewAddress(){
		int indexSelected = comboAddress.getSelectedIndex();
		if (indexSelected != currentComboAddressOption){
			ADDRESSES[] addresses = ADDRESSES.values();
			HashMap<I2P_CONTROL, String> hm = new HashMap<I2P_CONTROL, String>();
			hm.put(I2P_CONTROL.ADDRESS, addresses[indexSelected].getAddress());
			try {
				SetI2PControl.execute(hm);
				return REMOTE_SAVE_STATUS.SAVED_OK;
			} catch (InvalidPasswordException e) {
				StatusHandler.setDefaultStatus(DEFAULT_STATUS.INVALID_PASSWORD);
				return REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY;
			} catch (JSONRPC2SessionException e) {
				StatusHandler.setDefaultStatus(DEFAULT_STATUS.NOT_CONNECTED);
				return REMOTE_SAVE_STATUS.SAVE_FAILED_LOCALLY;
			} catch (InvalidParametersException e) {
				return REMOTE_SAVE_STATUS.SAVE_FAILED_REMOTELY;
			}
		} else {
			return REMOTE_SAVE_STATUS.NO_CHANGES;
		}
	}
*/
}
