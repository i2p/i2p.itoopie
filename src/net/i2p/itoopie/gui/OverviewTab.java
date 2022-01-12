package net.i2p.itoopie.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.EnumMap;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
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
	private final ConfigurationManager _conf;
	private final static int DEFAULT_INFO_UPDATE_INTERVAL = 30*1000; // Milliseconds.
	
	private final JLabel lblI2P;
	private final JLabel lblVersion;
	private final JLabel lblVersionSpecified;
	private final MultiLineLabel lblStatus;
	private final MultiLineLabel lblStatusSpecified;
	private final MultiLineLabel lblUptime;
	private final JLabel lblUptimeSpecified;
	private final MultiLineLabel lblNetworkStatus;
	private final MultiLineLabel lblNetworkStatusSpecified;
	private final BandwidthChart bwChart;
	private final ParticipatingTunnelsChart partTunnelChart;
	private volatile boolean running;

	public OverviewTab(String imageName, ConfigurationManager conf) {
		super(imageName);
		_conf = conf;
		super.setLayout(null);

		bwChart = new BandwidthChart(_conf);
		partTunnelChart = new ParticipatingTunnelsChart(_conf);
		ChartPanel pt = new ChartPanel(partTunnelChart);
		pt.setSize(300, 135);
		pt.setLocation(5, 10);
		pt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		ChartPanel cp = new ChartPanel(bwChart);
		cp.setSize(300, 135);
		cp.setLocation(5, 155);
		cp.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		add(pt);
		add(cp);

		lblI2P = new JLabel();
		add(lblI2P);
		lblI2P.setBounds(310, 30, 80, 15);
		lblI2P.setText("I2P");
		lblI2P.setHorizontalAlignment(SwingConstants.RIGHT);

		lblVersion = new JLabel();
		add(lblVersion);
		lblVersion.setBounds(310, 50, 80, 15);
		lblVersion.setText(Transl._t("Version:"));
		lblVersion.setHorizontalAlignment(SwingConstants.RIGHT);

		lblVersionSpecified = new JLabel();
		add(lblVersionSpecified);
		lblVersionSpecified.setBounds(400, 50, 140, 15);
		lblVersionSpecified.setHorizontalAlignment(SwingConstants.LEFT);
		
		
		lblUptime = new MultiLineLabel();
		add(lblUptime);
		lblUptime.setBounds(310, 70, 80, 30);
		// Don't set both! It doesn't work!
		//lblUptime.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUptime.setHorizontalTextAlignment(JLabel.RIGHT);
		lblUptime.setVerticalTextAlignment(JLabel.TOP);
		lblUptime.setText(Transl._t("Uptime:"));
		
		lblUptimeSpecified = new JLabel();
		add(lblUptimeSpecified);
		lblUptimeSpecified.setBounds(400, 70, 140, 15);
		lblUptimeSpecified.setHorizontalAlignment(SwingConstants.LEFT);

		
		lblStatus = new MultiLineLabel();
		add(lblStatus);
		lblStatus.setBounds(310, 140, 80, 30);
		// Don't set both! It doesn't work!
		//lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStatus.setHorizontalTextAlignment(JLabel.RIGHT);
		lblStatus.setVerticalTextAlignment(JLabel.TOP);
		lblStatus.setText(Transl._t("Status:"));

		lblStatusSpecified = new MultiLineLabel();
		add(lblStatusSpecified);
		lblStatusSpecified.setBounds(400, 140, 140, 30);
		lblStatusSpecified.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatusSpecified.setVerticalTextAlignment(JLabel.TOP);

		lblNetworkStatus = new MultiLineLabel();
		add(lblNetworkStatus);
		lblNetworkStatus.setBounds(310, 105, 80, 30);
		// Don't set both! It doesn't work!
		//lblNetworkStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNetworkStatus.setHorizontalTextAlignment(JLabel.RIGHT);
		lblNetworkStatus.setVerticalTextAlignment(JLabel.TOP);
		lblNetworkStatus.setText(Transl._t("Net Status:"));

		lblNetworkStatusSpecified = new MultiLineLabel();
		add(lblNetworkStatusSpecified);
		lblNetworkStatusSpecified.setBounds(400, 105, 130, 30);
		lblNetworkStatusSpecified.setHorizontalAlignment(SwingConstants.LEFT);
		lblNetworkStatusSpecified.setVerticalTextAlignment(JLabel.TOP);

		validate();
		
		
		final int updateInterval = _conf.getConf("overview.info.updateinterval", DEFAULT_INFO_UPDATE_INTERVAL);
		
		(new Thread("IToopie-OT"){
			@Override
			public void run() {
				running = true;
				while (running) {
					populateInfo();
					try {
						Thread.sleep(updateInterval);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}).start();
	}
	
	/**
	 * @since 0.0.4
	 */
	@Override
	public void destroy() {
		running = false;
		bwChart.destroy();
		partTunnelChart.destroy();
		super.removeNotify();
	}

	/**
	 * @since 0.0.4
	 */
	public void clearGraphs() {
		for (ITrace2D trace : bwChart.getTraces()) {
			trace.removeAllPoints();
		}
		for (ITrace2D trace : partTunnelChart.getTraces()) {
			trace.removeAllPoints();
		}
	}

	private void populateInfo(){
		try {
			EnumMap<ROUTER_INFO, Object> em = GetRouterInfo.execute(ROUTER_INFO.VERSION,
					ROUTER_INFO.UPTIME, ROUTER_INFO.STATUS, ROUTER_INFO.NETWORK_STATUS);
			
			
			lblVersionSpecified.setText((String) em.get(ROUTER_INFO.VERSION));
			// i2pd sends as String
			lblUptimeSpecified.setText(DataHelper.formatDuration(toLong(em.get(ROUTER_INFO.UPTIME))));
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
	
	/**
	 * Convert String to long for i2pd
	 * @since 0.0.4
	 */
	private static long toLong(Object o) {
		if (o == null)
			return 0;
		if (o instanceof Number)
			return ((Number) o).longValue();
		if (o instanceof String) {
			try {
				return Long.parseLong((String) o);
			} catch (NumberFormatException nfe) {}
		}
		return 0;
	}

	@Override
	public void onTabFocus(ChangeEvent e) {
		populateInfo();
	}

	/**
	 * Launch the application.
	 */
/*
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
	
*/	
	
}
