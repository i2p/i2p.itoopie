package net.i2p.itoopie.gui.component.chart;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.methods.GetRateStat;

/**
 *  Unused
 */
public class RateStatTracker extends Thread implements Tracker {
	
	/** Last read bw */
	private double m_value = 0;
	private final int updateInterval;
	
	/** Which RateStat to measure from the router */
	private String rateStat;
	
	/** Which period of a stat to measure */
	private long period;

	/**
	 * Start daemon that checks to current inbound bandwidth of the router.
	 */
	public RateStatTracker(String rateStat, long period, int interval) {
		this.rateStat = rateStat;
		this.period = period;
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
