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

public class ParticipatingTunnelsTracker extends Thread implements Tracker {
	
	/** Last read bw */
	private double m_value = 0;
	private final int updateInterval;

	/**
	 * Start daemon that checks to current inbound bandwidth of the router.
	 */
	public ParticipatingTunnelsTracker(int interval) {
		updateInterval = interval;
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
			EnumMap<ROUTER_INFO, Object> em = GetRouterInfo.execute(ROUTER_INFO.TUNNELS_PARTICIPATING);
			Long nbr = (Long) em.get(ROUTER_INFO.TUNNELS_PARTICIPATING);
			m_value = nbr.doubleValue();
		} catch (InvalidPasswordException e) {
			m_value = 0;
		} catch (JSONRPC2SessionException e) {
			m_value = 0;
		}
	}

	/**
	 * @since 0.0.4
	 */
	public double getData() { return m_value; }
}
