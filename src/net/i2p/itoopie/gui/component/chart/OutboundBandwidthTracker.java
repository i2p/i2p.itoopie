package net.i2p.itoopie.gui.component.chart;

import java.util.EnumMap;
import java.util.HashMap;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.methods.GetRateStat;
import net.i2p.itoopie.i2pcontrol.methods.GetRouterInfo;
import net.i2p.itoopie.i2pcontrol.methods.RouterInfo.ROUTER_INFO;

public class OutboundBandwidthTracker extends Thread {
	
	private static ConfigurationManager _conf = ConfigurationManager.getInstance();
	/** Last read bw */
	private double m_value = 0;
	
	/** Poll router for current ratestat every updateInterval seconds */
	private final static int DEFAULT_UPDATE_INTERVAL = 100; // Update every 100th ms

	private int updateInterval = _conf.getConf("graph.updateinterval", DEFAULT_UPDATE_INTERVAL);

	/**
	 * Start daemon that checks to current inbound bandwidth of the router.
	 */
	public OutboundBandwidthTracker() {
		this.setDaemon(true);
		this.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		while (true) {
			
			runOnce();
			try {
				Thread.sleep(updateInterval);
			} catch (InterruptedException e) {
				// nop
			}

		}
	}
	
	public synchronized void runOnce(){
		try {
			EnumMap<ROUTER_INFO, Object> em = GetRouterInfo.execute(ROUTER_INFO.BW_OUTBOUND_1S);
			double dbl = (Double) em.get(ROUTER_INFO.BW_OUTBOUND_1S);
			m_value = dbl / 1024; //Bytes -> KBytes
		} catch (InvalidPasswordException e) {
		} catch (JSONRPC2SessionException e) {
		}
	}
}
