package net.i2p.itoopie.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.EnumMap;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.views.ChartPanel;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.StatusHandler.DEFAULT_STATUS;
import net.i2p.itoopie.gui.component.BandwidthChart;
import net.i2p.itoopie.gui.component.MultiLineLabel;
import net.i2p.itoopie.gui.component.ParticipatingTunnelsChart;
import net.i2p.itoopie.gui.component.TabLogoPanel;
import net.i2p.itoopie.gui.component.multilinelabel.MultiLineLabelUI;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.methods.GetRouterInfo;
import net.i2p.itoopie.i2pcontrol.methods.GetRouterInfo.NETWORK_STATUS;
import net.i2p.itoopie.i2pcontrol.methods.RouterInfo.ROUTER_INFO;
import net.i2p.itoopie.util.DataHelper;

public class OverviewTab extends TabLogoPanel {
	private static ConfigurationManager _conf = ConfigurationManager.getInstance();
	private final static int DEFAULT_INFO_UPDATE_INTERVAL = 30*1000; // Milliseconds.
	
	JLabel lblI2P;
	JLabel lblVersion;
	JLabel lblVersionSpecified;
	JLabel lblStatus;
	JLabel lblStatusSpecified;
	JLabel lblUptime;
	JLabel lblUptimeSpecified;
	JLabel lblNetworkStatus;
	MultiLineLabel lblNetworkStatusSpecified;

	public OverviewTab(String imageName) {
		super(imageName);
		super.setLayout(null);

		final BandwidthChart bwChart = new BandwidthChart();
		Chart2D partTunnelChart = new ParticipatingTunnelsChart();
		ChartPanel pt = new ChartPanel(partTunnelChart);
		pt.setSize(300, 135);
		pt.setLocation(15, 10);
		pt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		ChartPanel cp = new ChartPanel(bwChart);
		cp.setSize(300, 135);
		cp.setLocation(15, 155);
		cp.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		add(pt);
		add(cp);

		lblI2P = new JLabel();
		add(lblI2P);
		lblI2P.setBounds(290, 30, 100, 15);
		lblI2P.setText("I2P");
		lblI2P.setHorizontalAlignment(SwingConstants.RIGHT);

		lblVersion = new JLabel();
		add(lblVersion);
		lblVersion.setBounds(290, 50, 100, 15);
		lblVersion.setText(Transl._("Version:"));
		lblVersion.setHorizontalAlignment(SwingConstants.RIGHT);

		lblVersionSpecified = new JLabel();
		add(lblVersionSpecified);
		lblVersionSpecified.setBounds(400, 50, 140, 15);
		lblVersionSpecified.setHorizontalAlignment(SwingConstants.LEFT);
//		lblVersionSpecified.setText("0.8.7-48rc"); // Delete Me
		
		
		lblUptime = new JLabel();
		add(lblUptime);
		lblUptime.setBounds(290, 70, 100, 15);
		lblUptime.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUptime.setText(Transl._("Uptime:"));
		
		lblUptimeSpecified = new JLabel();
		add(lblUptimeSpecified);
		lblUptimeSpecified.setBounds(400, 70, 140, 15);
		lblUptimeSpecified.setHorizontalAlignment(SwingConstants.LEFT);
//		lblUptimeSpecified.setText("93 min"); // Delete Me

		
		lblStatus = new JLabel();
		add(lblStatus);
		lblStatus.setBounds(290, 90, 100, 15);
		lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStatus.setText(Transl._("Status:"));

		lblStatusSpecified = new JLabel();
		add(lblStatusSpecified);
		lblStatusSpecified.setBounds(400, 90, 140, 15);
		lblStatusSpecified.setHorizontalAlignment(SwingConstants.LEFT);
//		lblStatusSpecified.setText("Rejecting Tunnels"); // Delete Me

		lblNetworkStatus = new JLabel();
		add(lblNetworkStatus);
		lblNetworkStatus.setBounds(290, 110, 100, 15);
		lblNetworkStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNetworkStatus.setText(Transl._("Netstatus:"));

		lblNetworkStatusSpecified = new MultiLineLabel();
		add(lblNetworkStatusSpecified);
		lblNetworkStatusSpecified.setBounds(400, 110, 130, 60);
		lblNetworkStatusSpecified.setHorizontalAlignment(SwingConstants.LEFT);
		lblNetworkStatusSpecified.setVerticalTextAlignment(JLabel.TOP);
//		lblNetworkStatusSpecified.setText("WARN-Firewalled with Inbound TCP Enabled".replace('-', ' ')); // Delete Me

		validate();
		
		
		final int updateInterval = _conf.getConf("overview.info.updateinterval", DEFAULT_INFO_UPDATE_INTERVAL);
		
		(new Thread(){
			@Override
			public void run() {
				while (true) {
					populateInfo();
					try {
						Thread.sleep(updateInterval);
					} catch (Exception e){} // Never stop.
				}
			}
		}).start();
	}
	
	private void populateInfo(){
		try {
			EnumMap<ROUTER_INFO, Object> em = GetRouterInfo.execute(ROUTER_INFO.VERSION,
					ROUTER_INFO.UPTIME, ROUTER_INFO.STATUS, ROUTER_INFO.NETWORK_STATUS);
			
			
			lblVersionSpecified.setText((String) em.get(ROUTER_INFO.VERSION));
			lblUptimeSpecified.setText(DataHelper.formatDuration((Long) em.get(ROUTER_INFO.UPTIME)));
			lblStatusSpecified.setText((String) em.get(ROUTER_INFO.STATUS));
			Long netStatus = (Long) em.get(ROUTER_INFO.NETWORK_STATUS);
			Integer intNetStatus = netStatus.intValue();
			NETWORK_STATUS enumNetStatus = GetRouterInfo.getEnum(intNetStatus);
			lblNetworkStatusSpecified.setText(enumNetStatus.toString());
			
			
			this.getRootPane().repaint(); // Repainting jlabel or jpanel is not enough.
			
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.CONNECTED);
		} catch (InvalidPasswordException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.INVALID_PASSWORD);
		} catch (JSONRPC2SessionException e) {
			StatusHandler.setDefaultStatus(DEFAULT_STATUS.NOT_CONNECTED);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	

	@Override
	public void onTabFocus(ChangeEvent e) {
		populateInfo();
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
					OverviewTab window = new OverviewTab("itoopie-opaque12");
					frame.add(window);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
}
