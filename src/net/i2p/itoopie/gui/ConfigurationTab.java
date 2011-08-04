package net.i2p.itoopie.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.gui.StatusHandler.DEFAULT_STATUS;
import net.i2p.itoopie.gui.component.LogoPanel;
import net.i2p.itoopie.gui.component.TabLogoPanel;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.methods.GetNetworkSetting;
import net.i2p.itoopie.i2pcontrol.methods.RouterManager.ROUTER_MANAGER;
import net.i2p.itoopie.i2pcontrol.methods.SetRouterManager;
import net.i2p.itoopie.i2pcontrol.methods.NetworkSetting.NETWORK_SETTING;
import net.i2p.itoopie.i2pcontrol.methods.SetNetworkSetting;

// The width of this panel (on ubuntu) will be the width of the main menu -24px.
public class ConfigurationTab extends TabLogoPanel {
	private final static String SETTINGS_READ = Transl._("Settings read from I2P router.");
	private static enum SAVE_STATUS{
		SAVE_FAILED_LOCALLY		{ public String toString(){return Transl._("Settings aren't valid, not saving.");} },
		SAVE_FAILED_REMOTELY	{ public String toString(){return Transl._("I2P router rejected settings.");} },
		SAVE_FAILED_NO_CONN		{ public String toString(){return Transl._("Not connected, unable to save.");} },
		SAVED_OK 				{ public String toString(){return Transl._("Saved settings on I2P router.");} },
		SAVED_RESTART_NEEDED	{ public String toString(){return Transl._("Saved settings on I2P router. I2P router needs to be restarted.");} }
	};
	
	// Components for the network panel
	private JLabel lblNetwork;
	private JLabel lblTCPPort;
	private JTextField txtTCPPort;
	private JLabel lblUDPPort;
	private JTextField txtUDPPort;
	private JLabel lblUPNP;
	private JCheckBox chkbxUPNP;
	// Components for the bandwidth panel
	private JLabel lblBW;
	private JLabel lblDownload;
	private JTextField txtDownload;
	private JLabel lblDownloadUnit;
	private JLabel lblUpload;
	private JTextField txtUpload;
	private JLabel lblUploadUnit;
	private JLabel lblShare;
	private JTextField txtShare;
	private JLabel lblShareUnit;
	
	private static final long serialVersionUID = 328657255717753899L;

	public ConfigurationTab(String imageName) {
		super(imageName);
		setLayout(null);
		
		JPanel networkPanel = new JPanel();
		add(networkPanel);
		networkPanel.setLayout(null);
		networkPanel.setOpaque(false);
		networkPanel.setBounds(0, 110, 262, 115);
		setupNetworkPanel(networkPanel);
		
		JPanel bwPanel = new JPanel();
		add(bwPanel);
		bwPanel.setLayout(null);
		bwPanel.setOpaque(false);
		bwPanel.setBounds(0, 5, 262, 105);
		setupBandwidthPanel(bwPanel);

		
		final JButton btnApply = new JButton(Transl._("Apply"));
		add(btnApply);
		btnApply.setBounds(442, 269, 82, 24);
		btnApply.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						applyNewSettings();
					}
				});
			}
		});
		btnApply.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent arg0) {
				getRootPane().repaint(); // Removes a transparent ring around button after mouse hovering.
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				getRootPane().repaint(); // Removes a transparent ring around button after mouse hovering.
			}
		});
		

		validate();
	}
	
	/**
	 * Setup bandwidth panel
	 */
	private void setupBandwidthPanel(JPanel bwPanel){
		lblBW = new JLabel();
		bwPanel.add(lblBW);
		lblBW.setBounds(10, 10, 120, 15);
		lblBW.setText(Transl._("Bandwidth:"));
		
		lblDownload = new JLabel();
		bwPanel.add(lblDownload);
		lblDownload.setBounds(40, 35, 100, 15);
		lblDownload.setText(Transl._("Download:"));
		
		txtDownload = new JTextField();
		bwPanel.add(txtDownload);
		txtDownload.setBounds(130, 35, 55, 20);

		lblDownloadUnit = new JLabel();
		bwPanel.add(lblDownloadUnit);
		lblDownloadUnit.setBounds(190, 37, 35, 15);
		lblDownloadUnit.setText(Transl._("KB/s"));
		
		lblUpload = new JLabel();
		bwPanel.add(lblUpload);
		lblUpload.setBounds(40, 60, 100, 15);
		lblUpload.setText(Transl._("Upload:"));
		
		txtUpload = new JTextField();
		bwPanel.add(txtUpload);
		txtUpload.setBounds(130, 60, 55, 20);

		lblUploadUnit = new JLabel();
		bwPanel.add(lblUploadUnit);
		lblUploadUnit.setBounds(190, 62, 35, 15);
		lblUploadUnit.setText(Transl._("KB/s"));
		
		lblShare = new JLabel();
		bwPanel.add(lblShare);
		lblShare.setBounds(40, 85, 100, 15);
		lblShare.setText(Transl._("Share:"));
		
		txtShare = new JTextField();
		bwPanel.add(txtShare);
		txtShare.setBounds(130, 85, 55, 20);
		
		lblShareUnit = new JLabel();
		bwPanel.add(lblShareUnit);
		lblShareUnit.setBounds(190, 87, 35, 15);
		lblShareUnit.setText(Transl._("%"));
	}
	
	/**
	 * Setup network panel
	 */
	private void setupNetworkPanel(JPanel networkPanel){
		lblNetwork = new JLabel();
		networkPanel.add(lblNetwork);
		lblNetwork.setBounds(10, 10, 75, 15);
		lblNetwork.setText(Transl._("Network:"));

		lblTCPPort = new JLabel();
		networkPanel.add(lblTCPPort);
		lblTCPPort.setBounds(40, 35, 75, 15);
		lblTCPPort.setText(Transl._("TCP port:"));

		txtTCPPort = new JTextField();
		networkPanel.add(txtTCPPort);
		txtTCPPort.setBounds(130, 35, 55, 20);
		txtTCPPort.setColumns(5);

		lblUDPPort = new JLabel();
		networkPanel.add(lblUDPPort);
		lblUDPPort.setBounds(40, 60, 75, 15);
		lblUDPPort.setText(Transl._("UDP port:"));

		txtUDPPort = new JTextField();
		networkPanel.add(txtUDPPort);
		txtUDPPort.setBounds(130, 60, 55, 20);

		
		lblUPNP = new JLabel();
		networkPanel.add(lblUPNP);
		lblUPNP.setBounds(40, 85, 75, 15);
		lblUPNP.setText(Transl._("UPNP:"));
		
		chkbxUPNP = new JCheckBox(Transl._("Enable UPNP"));
		chkbxUPNP.setOpaque(false);
		networkPanel.add(chkbxUPNP);
		chkbxUPNP.setBounds(127, 85, 120, 15);
	}
	
	private void populateSettings(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				try {
					EnumMap<NETWORK_SETTING, Object> em = GetNetworkSetting.execute(NETWORK_SETTING.TCP_PORT, NETWORK_SETTING.UDP_PORT, 
							NETWORK_SETTING.UPNP, NETWORK_SETTING.BW_IN, NETWORK_SETTING.BW_OUT, NETWORK_SETTING.BW_SHARE);
					
					txtTCPPort.setText((String) em.get(NETWORK_SETTING.TCP_PORT));
					txtUDPPort.setText((String) em.get(NETWORK_SETTING.UDP_PORT));
					boolean upnpValue = Boolean.parseBoolean((String) em.get(NETWORK_SETTING.UPNP));
					chkbxUPNP.setSelected(upnpValue);
					txtDownload.setText((String) em.get(NETWORK_SETTING.BW_IN));
					txtUpload.setText((String) em.get(NETWORK_SETTING.BW_OUT));
					txtShare.setText((String) em.get(NETWORK_SETTING.BW_SHARE));
					StatusHandler.setStatus(SETTINGS_READ);
					
				} catch (InvalidPasswordException e) {
					StatusHandler.setDefaultStatus(DEFAULT_STATUS.INVALID_PASSWORD);
				} catch (JSONRPC2SessionException e) {
					StatusHandler.setDefaultStatus(DEFAULT_STATUS.NOT_CONNECTED);
				}
			}
		});
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame();
					frame.setBounds(0, 0, Main.FRAME_WIDTH, Main.FRAME_HEIGHT);
					ConfigurationTab window = new ConfigurationTab("itoopie-opaque12");
					frame.add(window);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private SAVE_STATUS saveSettings(){
		String tcpPort = txtTCPPort.getText();
		String udpPort = txtUDPPort.getText();
		String upnp = Boolean.toString(chkbxUPNP.isSelected());
		String download = txtDownload.getText();
		String upload = txtUpload.getText();
		String share = txtShare.getText();
		
		int tcpPortOutput = -1; // Not a valid value.
		int udpPortOutput = -1; // Not a valid value.
		int downloadOutput;
		int uploadOutput;
		int shareOutput = -1; // Not a valid value.
		
		//Check TCP port
		try {
			tcpPortOutput = Integer.parseInt(tcpPort);
			if (tcpPortOutput < 1 || tcpPortOutput > 65535){
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._("' "+udpPort + " ' can not be interpreted as a UDP port.\n" + 
							"\nA port has to be a number in the range 1-65535."),
				    Transl._("Invalid port."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return SAVE_STATUS.SAVE_FAILED_LOCALLY;
		}
		
		//Check UDP port
		try {
			udpPortOutput = Integer.parseInt(udpPort);
			if (udpPortOutput < 1 || udpPortOutput > 65535){
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._("' "+udpPort + " ' can not be interpreted as a UDP port.\n" + 
							"\nA port has to be a number in the range 1-65535."),
				    Transl._("Invalid port."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return SAVE_STATUS.SAVE_FAILED_LOCALLY;
		}
		
		//Check download
		try {
			downloadOutput = Integer.parseInt(download);
			if (downloadOutput < 1){
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._("' "+download + " ' can not be interpreted as an download spped.\n" + 
							"\nA port has to be a number larger than 0."),
				    Transl._("Invalid download speed."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return SAVE_STATUS.SAVE_FAILED_LOCALLY;
		}
		
		//Check Upload
		try {
			uploadOutput = Integer.parseInt(upload);
			if (uploadOutput < 1){
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._("' "+upload + " ' can not be interpreted as an upload spped.\n" + 
							"\nA port has to be a number larger than 0."),
				    Transl._("Invalid upload speed."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			return SAVE_STATUS.SAVE_FAILED_LOCALLY;
		}
		
		//Check share
		try {
			shareOutput = Integer.parseInt(share);
			if (shareOutput < 0 || shareOutput > 100){
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e){
			JOptionPane.showConfirmDialog(
				    this,
				    Transl._("' "+share + " ' can not be interpreted as a percentage.\n" + 
							"\nThe percentage has to be a number in the range 0-100."),
				    Transl._("Invalid share percentage."),
				    JOptionPane.DEFAULT_OPTION,
				    JOptionPane.ERROR_MESSAGE);
		}
		
		HashMap<NETWORK_SETTING, String> output = new HashMap<NETWORK_SETTING, String>();
		
		output.put(NETWORK_SETTING.TCP_PORT, tcpPortOutput+"");
		output.put(NETWORK_SETTING.UDP_PORT, udpPortOutput+"");
		output.put(NETWORK_SETTING.BW_IN, downloadOutput+"");
		output.put(NETWORK_SETTING.BW_OUT, uploadOutput+"");
		output.put(NETWORK_SETTING.BW_SHARE, shareOutput+"");
		
		try {
			EnumMap<NETWORK_SETTING, Object> em = SetNetworkSetting.execute(output);
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.CONNECTED);
			boolean settingsSaved = (Boolean) em.get(NETWORK_SETTING.SETTINGS_SAVED);
			boolean restartNeeded = (Boolean) em.get(NETWORK_SETTING.RESTART_NEEDED);
			if (settingsSaved && restartNeeded){
				return SAVE_STATUS.SAVED_RESTART_NEEDED;
			} else if (settingsSaved){
				return SAVE_STATUS.SAVED_OK;
			} else {
				return SAVE_STATUS.SAVE_FAILED_REMOTELY;
			}
		} catch (InvalidPasswordException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.INVALID_PASSWORD);
			return SAVE_STATUS.SAVE_FAILED_LOCALLY;
		} catch (JSONRPC2SessionException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.NOT_CONNECTED);
			return SAVE_STATUS.SAVE_FAILED_NO_CONN;
		} catch (InvalidParametersException e) {
			return SAVE_STATUS.SAVE_FAILED_REMOTELY;
		}
	}
	
	private void applyNewSettings(){
		switch (saveSettings()){
			case SAVED_RESTART_NEEDED: 
				StatusHandler.setStatus(SAVE_STATUS.SAVED_RESTART_NEEDED.toString());
				int n = JOptionPane.showConfirmDialog(
					    this,
					    Transl._("The new settings have been applied,\n" + "" +
					    		"but the I2P router needs to be restarted for some to take effect.\n" + 
								"\nWould you like to restart the I2P router now?"),
					    Transl._("Restart needed for new settings."),
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.INFORMATION_MESSAGE);
				if (n == JOptionPane.YES_OPTION){
					try {
						HashMap<ROUTER_MANAGER, String> hm = new HashMap<ROUTER_MANAGER,String>();
						hm.put(ROUTER_MANAGER.RESTART, null);
						SetRouterManager.execute(hm);
						StatusHandler.setStatus(Transl._("Restarting I2P node.. "));
					} catch (InvalidPasswordException e) {
						StatusHandler.setStatus(Transl._("Restart failed: ") + DEFAULT_STATUS.INVALID_PASSWORD);
					} catch (JSONRPC2SessionException e) {
						StatusHandler.setStatus(Transl._("Restart failed: ") + DEFAULT_STATUS.NOT_CONNECTED);
					}
				}
				break;
			case SAVED_OK:
				StatusHandler.setStatus(SAVE_STATUS.SAVED_OK.toString());
				break;
			case SAVE_FAILED_REMOTELY:
				StatusHandler.setStatus(SAVE_STATUS.SAVE_FAILED_REMOTELY.toString());
				break;
			case SAVE_FAILED_LOCALLY:
				StatusHandler.setStatus(SAVE_STATUS.SAVE_FAILED_LOCALLY.toString());
				break;
			case SAVE_FAILED_NO_CONN:
				StatusHandler.setStatus(SAVE_STATUS.SAVE_FAILED_NO_CONN.toString());
				break;
		}
	}

	@Override
	public void onTabFocus(ChangeEvent e) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				populateSettings();
			}
		});			
	}
}