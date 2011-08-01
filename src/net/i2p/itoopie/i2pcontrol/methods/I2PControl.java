package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;


/**
 * Describes the ways a I2P router operation can be altered.
 * @author hottuna
 */
public class I2PControl{
	private final static HashMap<String,I2P_CONTROL> enumMap;

	
	/**
	 * Describes the ways a I2P router operation can be altered.
	 * @author hottuna
	 */
	public enum I2P_CONTROL implements Remote{
		PASSWORD { 			public boolean isReadable(){ return false;}
							public boolean isWritable(){ return true;}  
							public String toString() { return "i2pcontrol.password"; }},
						
		PORT { 				public boolean isReadable(){ return false;}	
							public boolean isWritable(){ return true;}  
							public String toString() { return "i2pcontrol.port"; }},
							
		ADDRESS { 	public boolean isReadable(){ return false;}	
							public boolean isWritable(){ return true;}  
							public String toString() { return "i2pcontrol.address"; }}
							
							
	};
		
	static {
		enumMap = new HashMap<String,I2P_CONTROL>();
		for (I2P_CONTROL n : I2P_CONTROL.values()){
			enumMap.put(n.toString(), n);
		}
	}
	
	public static I2P_CONTROL getEnum(String key){
		return enumMap.get(key);
	}
}
