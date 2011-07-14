package net.i2p.itoopie.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.gui.StatusHandler.DEFAULT_STATUS;
import net.i2p.itoopie.gui.component.LogoPanel;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.methods.GetNetworkSetting;
import net.i2p.itoopie.i2pcontrol.methods.NetworkSetting.NETWORK_SETTING;

// The width of this panel (on ubuntu) will be the width of the main menu -24px.
public class ConfigurationPanel extends LogoPanel {
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

	public ConfigurationPanel(String imageName) {
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
		btnApply.setBounds(450, 272, 75, 24);
		btnApply.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Clicked");
			}
		});
		
		

		
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				System.out.println("Running populate thread.");
				populate();
				System.out.println("Populate thread done.");
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
		networkPanel.add(chkbxUPNP);
		chkbxUPNP.setBounds(127, 85, 120, 15);
	}
	
	private void populate(){
		try {
			HashMap hm = GetNetworkSetting.execute(NETWORK_SETTING.TCP_PORT, NETWORK_SETTING.UDP_PORT, 
					NETWORK_SETTING.UPNP, NETWORK_SETTING.BW_IN, NETWORK_SETTING.BW_OUT);
			
			System.out.println("TCP_PORT: " + (String) hm.get(NETWORK_SETTING.TCP_PORT.toString()));
			System.out.println("UDP_PORT: " + (String) hm.get(NETWORK_SETTING.UDP_PORT.toString()));

			txtTCPPort.setText((String) hm.get(NETWORK_SETTING.TCP_PORT.toString()));
			txtUDPPort.setText((String) hm.get(NETWORK_SETTING.UDP_PORT.toString()));
			boolean upnpValue = Boolean.parseBoolean((String) hm.get(NETWORK_SETTING.UPNP));
			chkbxUPNP.setSelected(upnpValue);
			txtDownload.setText((String) hm.get(NETWORK_SETTING.BW_IN));
			txtUpload.setText((String) hm.get(NETWORK_SETTING.BW_OUT));
			txtShare.setText((String) hm.get(NETWORK_SETTING.BW_SHARE));
			txtShare.setText("TEST");
		} catch (InvalidPasswordException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.INVALID_PASSWORD);
		} catch (JSONRPC2SessionException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.NOT_CONNECTED);
		}
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
					ConfigurationPanel window = new ConfigurationPanel("itoopie-opaque12");
					frame.add(window);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}