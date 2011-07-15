package net.i2p.itoopie.i2pcontrol.methods;

/**
 * Describes how an option can be used against a server implementing the I2PControl API.
 * @author hottuna
 *
 */
public interface Remote{	
	
	/**
	 * @return - Does an I2PControl server allow this setting to saved.
	 */
	public boolean isWritable(); 
	
	/**
	 * @return - Does an I2PControl server allow this setting to be read.
	 */
	public boolean isReadable();
}