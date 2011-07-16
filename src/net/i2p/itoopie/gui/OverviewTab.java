package net.i2p.itoopie.gui;

import java.awt.Color;
import java.awt.EventQueue;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.views.ChartPanel;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import net.i2p.itoopie.gui.component.BandwidthChart;
import net.i2p.itoopie.gui.component.MultiLineLabel;
import net.i2p.itoopie.gui.component.ParticipatingTunnelsChart;
import net.i2p.itoopie.gui.component.TabLogoPanel;
import net.i2p.itoopie.gui.component.multilinelabel.MultiLineLabelUI;
import net.i2p.itoopie.i18n.Transl;

public class OverviewTab extends TabLogoPanel {
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

		Chart2D bwChart = BandwidthChart.getChart();
		Chart2D partTunnelChart = ParticipatingTunnelsChart.getChart();
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
		lblI2P.setBounds(285, 30, 100, 15);
		lblI2P.setText("I2P");
		lblI2P.setHorizontalAlignment(SwingConstants.RIGHT);

		lblVersion = new JLabel();
		add(lblVersion);
		lblVersion.setBounds(285, 50, 100, 15);
		lblVersion.setText(Transl._("Version:"));
		lblVersion.setHorizontalAlignment(SwingConstants.RIGHT);

		lblVersionSpecified = new JLabel();
		add(lblVersionSpecified);
		lblVersionSpecified.setBounds(395, 50, 140, 15);
		lblVersionSpecified.setHorizontalAlignment(SwingConstants.LEFT);
		lblVersionSpecified.setText("0.8.7-48rc"); // Delete Me
		
		
		lblUptime = new JLabel();
		add(lblUptime);
		lblUptime.setBounds(285, 70, 100, 15);
		lblUptime.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUptime.setText(Transl._("Uptime:"));
		
		lblUptimeSpecified = new JLabel();
		add(lblUptimeSpecified);
		lblUptimeSpecified.setBounds(395, 70, 140, 15);
		lblUptimeSpecified.setHorizontalAlignment(SwingConstants.LEFT);
		lblUptimeSpecified.setText("93 min"); // Delete Me

		
		lblStatus = new JLabel();
		add(lblStatus);
		lblStatus.setBounds(285, 90, 100, 15);
		lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStatus.setText(Transl._("Status:"));

		lblStatusSpecified = new JLabel();
		add(lblStatusSpecified);
		lblStatusSpecified.setBounds(395, 90, 140, 15);
		lblStatusSpecified.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatusSpecified.setText("Rejecting Tunnels"); // Delete Me

		lblNetworkStatus = new JLabel();
		add(lblNetworkStatus);
		lblNetworkStatus.setBounds(285, 110, 100, 15);
		lblNetworkStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNetworkStatus.setText(Transl._("Netstatus:"));

		lblNetworkStatusSpecified = new MultiLineLabel();
		add(lblNetworkStatusSpecified);
		lblNetworkStatusSpecified.setBounds(395, 110, 130, 60);
		lblNetworkStatusSpecified.setHorizontalAlignment(SwingConstants.LEFT);
		lblNetworkStatusSpecified.setVerticalTextAlignment(JLabel.TOP);
		lblNetworkStatusSpecified.setText("WARN-Firewalled with Inbound TCP Enabled".replace('-', ' ')); // Delete Me

		validate();
	}

	@Override
	public void onTabFocus(ChangeEvent e) {
		// Do thigns when shown?
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
