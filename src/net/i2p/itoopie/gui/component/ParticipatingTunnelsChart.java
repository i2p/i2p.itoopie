package net.i2p.itoopie.gui.component;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterNumber;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyMinimumViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterPolyline;
import info.monitorenter.gui.chart.views.ChartPanel;
import info.monitorenter.util.Range;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.component.chart.DummyDataCollector;
import net.i2p.itoopie.gui.component.chart.RateStatTracker;
import net.i2p.itoopie.gui.component.chart.ObjRecorder2Trace2DAdapter;
import net.i2p.itoopie.i18n.Transl;


public class ParticipatingTunnelsChart {
	private static ConfigurationManager _conf = ConfigurationManager.getInstance();
	private final static int DEFAULT_UPDATE_INTERVAL = 10000; // Update every 1000th ms
	private final static int DEFAULT_GRAPH_INTERVAL = 2*3600*1000; // The graph will cover a maximum of 2hrs
	private final static String DATE_FORMAT = "HH:mm:ss";
	
	public static Chart2D getChart(){
		int updateInterval = _conf.getConf("graph.updateinterval", DEFAULT_UPDATE_INTERVAL);
		int graphInterval = _conf.getConf("graph.graphinterval", DEFAULT_GRAPH_INTERVAL);
		
	    Chart2D chart = new Chart2D();
	    chart.setUseAntialiasing(true);
	    chart.setMinPaintLatency(20);
	    ITrace2D dataPartTunnels = new Trace2DLtd(  graphInterval/updateInterval  );
	    dataPartTunnels.setStroke(new BasicStroke(1));
	    dataPartTunnels.setColor(new Color(255, 0, 0, 255));
	    dataPartTunnels.setName(Transl._("Number of tunnels we are participating in."));
	
	    ITracePainter<?> dotPainter = new TracePainterPolyline();
	    dataPartTunnels.setTracePainter(dotPainter);
	    chart.addTrace(dataPartTunnels);

	    final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    
	    chart.getAxisX().setFormatter(new LabelFormatterDate(sdf));
	    chart.getAxisX().setPaintGrid(true);
	    chart.getAxisX().setAxisTitle(new AxisTitle(Transl._("Time")));
	
	    DecimalFormat df = new DecimalFormat("0 ; 0");
	    chart.getAxisY().setFormatter(new LabelFormatterNumber(df));
	    chart.getAxisY().setPaintGrid(true);
	    chart.getAxisY().setAxisTitle(new AxisTitle(""));
	
	    // force ranges:
	    chart.getAxisY().setRangePolicy(new RangePolicyMinimumViewport(new Range(0, 20)));

	    new ObjRecorder2Trace2DAdapter(dataPartTunnels, new RateStatTracker("tunnel.participatingTunnels", 60*1000L), "m_value", updateInterval);
	    //new ObjRecorder2Trace2DAdapter(dataPartTunnels, new DummyDataCollector(0.5, 1000), "m_number", updateInterval);
	    return chart;
	}
	
	  public static void main(final String[] args) {
		  JFrame frame = new JFrame();
		  Container contentPane = frame.getContentPane();
		  contentPane.setLayout(new BorderLayout());
		  contentPane.add(new ChartPanel(getChart()), BorderLayout.CENTER);
		  //frame.add(new ChartPanel(getChart()));
		  frame.setLocation(200, 300);
		  frame.setSize(700, 210);
		  frame.setResizable(true);
		  frame.setVisible(true);
	  }
}
