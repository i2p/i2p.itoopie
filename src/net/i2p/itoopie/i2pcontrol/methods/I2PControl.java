package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;

import net.i2p.itoopie.i18n.Transl;


/**
 * Describes the ways a I2P router operation can be altered.
 * @author hottuna
 */
public class I2PControl{
	private final static HashMap<String,I2P_CONTROL> enumMap;
	private static final HashMap<String, ADDRESSES> reverseAddressMap;
	private interface Address { public String getAddress();}
	
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
							
		ADDRESS { 			public boolean isReadable(){ return true;}	
							public boolean isWritable(){ return true;}  
							public String toString() { return "i2pcontrol.address"; }}
							
							
	};
	public enum ADDRESSES implements Address {
		
		LOCAL { 			public String getAddress(){ return "127.0.0.1";}
							public String toString(){ return Transl._("local host (127.0.0.1)"); }},
		
		/*LAN_192_168 {		public String getAddress(){ return "192.168.0.0";}
							public String toString(){ return Transl._("lan host (192.168.*.*)"); }},
		
		LAN_10 {			public String getAddress(){ return "10.0.0.0";}
							public String toString(){ return Transl._("lan host (10.*.*.*)"); }},
		*/
		ANY {				public String getAddress(){ return "0.0.0.0";}
							public String toString(){ return Transl._("any host (*.*.*.*)"); }}
	};
		
	static {
		enumMap = new HashMap<String,I2P_CONTROL>();
		for (I2P_CONTROL n : I2P_CONTROL.values()){
			enumMap.put(n.toString(), n);
		}
		
		reverseAddressMap = new HashMap<String,ADDRESSES>();
		for (ADDRESSES n : ADDRESSES.values()){
			reverseAddressMap.put(n.getAddress(), n);
		}
	}
	
	public static I2P_CONTROL getEnum(String key){
		return enumMap.get(key);
	}
	
	public static ADDRESSES getAddressEnum(String key){
		return reverseAddressMap.get(key);
	}
	
}
