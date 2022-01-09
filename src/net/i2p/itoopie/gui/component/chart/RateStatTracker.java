package net.i2p.itoopie.gui.component.chart;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.methods.GetRateStat;

public class RateStatTracker extends Thread implements Tracker {
	
	private static ConfigurationManager _conf = ConfigurationManager.getInstance();
	/** Last read bw */
	private double m_value = 0;
	
	/** Poll router for current ratestat every updateInterval seconds */
	private final static int DEFAULT_UPDATE_INTERVAL = 100; // Update every 100th ms

	private int updateInterval = _conf.getConf("graph.updateinterval", DEFAULT_UPDATE_INTERVAL);
	
	/** Which RateStat to measure from the router */
	private String rateStat;
	
	/** Which period of a stat to measure */
	private long period;

	/**
	 * Start daemon that checks to current inbound bandwidth of the router.
	 */
	public RateStatTracker(String rateStat, long period) {
		this.rateStat = rateStat;
		this.period = period;
		this.setDaemon(true);
		this.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		while (true) {
			try {
				m_value = GetRateStat.execute(rateStat, period);
			} catch (InvalidPasswordException e1) {
			} catch (JSONRPC2SessionException e1) {
			} catch (InvalidParametersException e1) {
			}

			try {
				Thread.sleep(updateInterval);
			} catch (InterruptedException e) {
				// nop
			}

		}
	}

	/**
	 * @since 0.0.4
	 */
	public double getData() { return m_value; }
}
