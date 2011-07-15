package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;


/**
 * Describes very basic I2P router information.
 * @author hottuna
 */
public class RouterInfo{
	private final static HashMap<String,ROUTER_INFO> enumMap;

	
	/**
	 * Describes very basic I2P router information.
	 * @author hottuna
	 */
	public enum ROUTER_INFO implements Remote{
		VERSION { 			public boolean isReadable(){ return true;}
							public boolean isWritable(){ return false;}  
							public String toString() { return "i2p.router.version"; }},
						
		UPTIME { 			public boolean isReadable(){ return true;}	
							public boolean isWritable(){ return false;}  
							public String toString() { return "i2p.router.uptime"; }},
						
		STATUS { 			public boolean isReadable(){ return true;}	
							public boolean isWritable(){ return false;}  
							public String toString() { return "i2p.router.status"; }},
						
		NETWORK_STATUS {	public boolean isReadable(){ return true;}	
							public boolean isWritable(){ return false;}  
							public String toString() { return "i2p.router.net.status"; }}
	};
		
	static {
		enumMap = new HashMap<String,ROUTER_INFO>();
		for (ROUTER_INFO n : ROUTER_INFO.values()){
			enumMap.put(n.toString(), n);
		}
	}
	
	public static ROUTER_INFO getEnum(String key){
		return enumMap.get(key);
	}
}
