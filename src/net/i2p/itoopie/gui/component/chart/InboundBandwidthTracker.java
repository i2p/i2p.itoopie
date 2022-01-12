package net.i2p.itoopie.gui.component.chart;

import java.util.EnumMap;
import java.util.HashMap;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.methods.GetRateStat;
import net.i2p.itoopie.i2pcontrol.methods.GetRouterInfo;
import net.i2p.itoopie.i2pcontrol.methods.RouterInfo.ROUTER_INFO;

public class InboundBandwidthTracker extends Thread implements Tracker {
	
	/** Last read bw */
	private double m_value = 0;
	private final int updateInterval;
	private volatile boolean running;

	/**
	 * Start daemon that checks to current inbound bandwidth of the router.
	 */
	public InboundBandwidthTracker(int interval) {
		super("IToopie-IBT");
		updateInterval = interval;
		this.setDaemon(true);
		this.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		running = true;
		while (running) {
			
			runOnce();
			try {
				Thread.sleep(updateInterval);
			} catch (InterruptedException e) {
				break;
			}

		}
	}
	
	public synchronized void runOnce(){
		try {
			EnumMap<ROUTER_INFO, Object> em = GetRouterInfo.execute(ROUTER_INFO.BW_INBOUND_1S);
			double dbl = (Double) em.get(ROUTER_INFO.BW_INBOUND_1S);
			m_value = dbl / 1024; //Bytes -> KBytes
		} catch (InvalidPasswordException e) {
			m_value = 0;
		} catch (JSONRPC2SessionException e) {
			m_value = 0;
		}
	}

	/**
	 * @since 0.0.4
	 */
	public void kill() {
		running = false;
		interrupt();
	}

	/**
	 * @since 0.0.4
	 */
	public double getData() { return m_value; }
}
