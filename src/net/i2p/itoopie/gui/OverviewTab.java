package net.i2p.itoopie.gui;

import java.awt.Color;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.views.ChartPanel;

import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;

import net.i2p.itoopie.gui.component.BandwidthChart;
import net.i2p.itoopie.gui.component.ParticipatingTunnelsChart;
import net.i2p.itoopie.gui.component.TabLogoPanel;

public class OverviewTab extends TabLogoPanel {

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
		cp.setSize(300,135);
		cp.setLocation(15, 155);
		cp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		add(pt);
		add(cp);
	}
	
	

	@Override
	public void onTabFocus(ChangeEvent e) {
		System.out.println("OverviewTab onTabFocus()");

	}

}
