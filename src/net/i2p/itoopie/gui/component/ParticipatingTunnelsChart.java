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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.component.chart.ParticipatingTunnelsTracker;
import net.i2p.itoopie.gui.component.chart.ObjRecorder2Trace2DAdapter;
import net.i2p.itoopie.i18n.Transl;


public class ParticipatingTunnelsChart extends Chart2D {
	private final ConfigurationManager _conf;
	private final static int DEFAULT_UPDATE_INTERVAL = 10000; // Update every 1000th ms
	private final static int DEFAULT_GRAPH_INTERVAL = 3600*1000; // The graph will cover a maximum of this time
	private ParticipatingTunnelsTracker partTunnelTracker;	
	private ObjRecorder2Trace2DAdapter partTunnelAdapter;
	
	public ParticipatingTunnelsChart(ConfigurationManager conf) {
		super();
		_conf = conf;
		int updateInterval = _conf.getConf("graph.updateinterval", DEFAULT_UPDATE_INTERVAL);
		int graphInterval = _conf.getConf("graph.graphinterval", DEFAULT_GRAPH_INTERVAL);
		
	    setUseAntialiasing(true);
	    setMinPaintLatency(20);
	    ITrace2D dataPartTunnels = new Trace2DLtd(  graphInterval/updateInterval  );
	    dataPartTunnels.setStroke(new BasicStroke(1));
	    dataPartTunnels.setColor(new Color(255, 0, 0, 255));
	    dataPartTunnels.setName(Transl._t("Number of tunnels we are participating in."));
	
	    ITracePainter<?> dotPainter = new TracePainterPolyline();
	    dataPartTunnels.setTracePainter(dotPainter);
	    addTrace(dataPartTunnels);

	    final SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT);
	    String pattern = sdf.toLocalizedPattern();
	    if (pattern.endsWith(" a")) {
		// no room for AM/PM
		sdf.applyLocalizedPattern(pattern.substring(0, pattern.length() - 2));
	    }
	    
	    getAxisX().setFormatter(new LabelFormatterDate(sdf));
	    getAxisX().setPaintGrid(true);
	    //getAxisX().setAxisTitle(new AxisTitle(Transl._t("Time")));
	    getAxisX().setAxisTitle(new AxisTitle(""));
	
	    DecimalFormat df = new DecimalFormat("0 ; 0");
	    getAxisY().setFormatter(new LabelFormatterNumber(df));
	    getAxisY().setPaintGrid(true);
	    getAxisY().setAxisTitle(new AxisTitle(""));
	
	    // force ranges:
	    getAxisY().setRangePolicy(new RangePolicyMinimumViewport(new Range(0, 20)));

	    partTunnelTracker = new ParticipatingTunnelsTracker(updateInterval);
	    partTunnelAdapter = new ObjRecorder2Trace2DAdapter(dataPartTunnels, partTunnelTracker, "m_value", updateInterval/2);
	}
	
/*
	  public static void main(final String[] args) {
		  JFrame frame = new JFrame();
		  Container contentPane = frame.getContentPane();
		  contentPane.setLayout(new BorderLayout());
		  contentPane.add(new ChartPanel(new ParticipatingTunnelsChart()), BorderLayout.CENTER);
		  //frame.add(new ChartPanel(getChart()));
		  frame.setLocation(200, 300);
		  frame.setSize(700, 210);
		  frame.setResizable(true);
		  frame.setVisible(true);
	  }
*/
}
