package net.i2p.itoopie.gui.component.chart;

public class DummyDataCollector extends Thread {
	/** Streches or compresses the grade of jumping of the internal number. */
	protected double m_factor;

	/** The bumping number. */
	protected double m_number = 0;

	/** The propability of an increase versus a decrease of the bumped number. */
	protected double m_plusminus = 0.5;

	/** Needed for randomization of bumping the number. */
	protected java.util.Random m_randomizer = new java.util.Random();

	/**
	 * Creates an instance.
	 * <p>
	 * 
	 * @param plusminus
	 *            probability to increase or decrease the number each step.
	 * @param factor
	 *            affects the amplitude of the number (severity of jumps).
	 */
	public DummyDataCollector(final double plusminus, final int factor) {

		if (plusminus < 0 || plusminus > 1) {
			System.out
					.println(this.getClass().getName()
							+ " ignores constructor-passed value. Must be between 0.0 and 1.0!");
		} else {
			this.m_plusminus = plusminus;
		}
		this.m_factor = factor;
		this.setDaemon(true);
		this.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		while (true) {
			double rand = this.m_randomizer.nextDouble();
			if (rand < this.m_plusminus) {
				this.m_number += this.m_randomizer.nextDouble() * this.m_factor;
			} else {
				this.m_number -= this.m_randomizer.nextDouble() * this.m_factor;
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// nop
			}

		}
	}
}
