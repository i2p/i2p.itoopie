package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;


/**
 * Describes the ways a I2P router can be restarted.
 * @author hottuna
 */
public class RouterManager{
	private final static HashMap<String,ROUTER_MANAGER> enumMap;

	
	/**
	 * Describes the ways a I2P router can be restarted.
	 * @author hottuna
	 */
	public enum ROUTER_MANAGER implements Remote{
		RESTART { 			public boolean isReadable(){ return true;}
							public boolean isWritable(){ return false;}  
							public String toString() { return "Restart"; }},
						
		SHUTDOWN { 			public boolean isReadable(){ return true;}	
							public boolean isWritable(){ return false;}  
							public String toString() { return "Shutdown"; }},
						
		RESTART_GRACEFUL { 	public boolean isReadable(){ return true;}	
							public boolean isWritable(){ return false;}  
							public String toString() { return "RestartGraceful"; }},
						
		SHUTDOWN_GRACEFUL {	public boolean isReadable(){ return true;}	
							public boolean isWritable(){ return false;}  
							public String toString() { return "ShutdownGraceful"; }},
							
		RESEED {			public boolean isReadable(){ return true;}	
							public boolean isWritable(){ return false;}  
							public String toString() { return "Reseed"; }}
	};
		
	static {
		enumMap = new HashMap<String,ROUTER_MANAGER>();
		for (ROUTER_MANAGER n : ROUTER_MANAGER.values()){
			enumMap.put(n.toString(), n);
		}
	}
	
	public static ROUTER_MANAGER getEnum(String key){
		return enumMap.get(key);
	}
}
