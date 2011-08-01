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
import net.i2p.itoopie.gui.component.chart.InboundBandwidthTracker;
import net.i2p.itoopie.gui.component.chart.OutboundBandwidthTracker;
import net.i2p.itoopie.gui.component.chart.ObjRecorder2Trace2DAdapter;
import net.i2p.itoopie.i18n.Transl;


public class BandwidthChart extends Chart2D{
	private static ConfigurationManager _conf = ConfigurationManager.getInstance();
	private final static int DEFAULT_UPDATE_INTERVAL = 10000; // Update every 2500th ms
	private final static int DEFAULT_GRAPH_INTERVAL = 2*3600*1000; // The graph will cover a maximum of 2hrs
	private final static String DATE_FORMAT = "HH:mm:ss";
	private ObjRecorder2Trace2DAdapter bwInAdapter;
	private ObjRecorder2Trace2DAdapter bwOutAdapter;
	private InboundBandwidthTracker bwInTracker;
	private OutboundBandwidthTracker bwOutTracker;
	
	public BandwidthChart(){
		super();
		
		int updateInterval = _conf.getConf("graph.updateinterval", DEFAULT_UPDATE_INTERVAL);
		int graphInterval = _conf.getConf("graph.graphinterval", DEFAULT_GRAPH_INTERVAL);
		
	    setUseAntialiasing(true);
	    setMinPaintLatency(20);
	    ITrace2D dataBWIn = new Trace2DLtd( graphInterval/updateInterval );
	    dataBWIn.setStroke(new BasicStroke(1));
	    dataBWIn.setColor(new Color(255, 0, 0, 255));
	    dataBWIn.setName(Transl._("Bandwidth In [KB/s]"));
	
	    ITracePainter<?> dotPainter = new TracePainterPolyline();
	    dataBWIn.setTracePainter(dotPainter);
	    addTrace(dataBWIn);
	
	    ITrace2D dataBWOut = new Trace2DLtd( graphInterval/updateInterval );
	    dataBWOut.setStroke(new BasicStroke(1));
	    dataBWOut.setColor(new Color(0, 0, 255, 255));
	    dataBWOut.setName(Transl._("Bandwidth Out [KB/s]"));
	
	    dataBWOut.setTracePainter(dotPainter);
	    addTrace(dataBWOut);
	    
	    final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    
	    getAxisX().setFormatter(new LabelFormatterDate(sdf));
	    getAxisX().setPaintGrid(true);
	    getAxisX().setAxisTitle(new AxisTitle(Transl._("Time")));
	
	    DecimalFormat df = new DecimalFormat("0 ; 0");
	    getAxisY().setFormatter(new LabelFormatterNumber(df));
	    getAxisY().setPaintGrid(true);
	    getAxisY().setAxisTitle(new AxisTitle(""));
	
	    // force ranges:
	    getAxisY().setRangePolicy(new RangePolicyMinimumViewport(new Range(0, 5)));

	    bwInTracker = new InboundBandwidthTracker();
	    bwOutTracker = new OutboundBandwidthTracker();
	    
	    bwInAdapter = new ObjRecorder2Trace2DAdapter(dataBWIn, bwInTracker, "m_value", updateInterval/2);
	    bwOutAdapter = new ObjRecorder2Trace2DAdapter(dataBWOut, bwOutTracker, "m_value", updateInterval/2);
	}
	
	
	public static void main(final String[] args) {
		  JFrame frame = new JFrame();
		  Container contentPane = frame.getContentPane();
		  contentPane.setLayout(new BorderLayout());
		  contentPane.add(new ChartPanel(new BandwidthChart()), BorderLayout.CENTER);
		  //frame.add(new ChartPanel(getChart()));
		  frame.setLocation(200, 300);
		  frame.setSize(700, 210);
		  frame.setResizable(true);
		  frame.setVisible(true);
	  }
}
