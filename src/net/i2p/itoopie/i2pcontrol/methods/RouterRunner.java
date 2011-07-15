package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;


/**
 * Describes the ways a I2P router can be restarted.
 * @author hottuna
 */
public class RouterRunner{
	private final static HashMap<String,ROUTER_RUNNER> enumMap;

	
	/**
	 * Describes the ways a I2P router can be restarted.
	 * @author hottuna
	 */
	public enum ROUTER_RUNNER implements Remote{
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
							public String toString() { return "ShutdownGraceful"; }}
	};
		
	static {
		enumMap = new HashMap<String,ROUTER_RUNNER>();
		for (ROUTER_RUNNER n : ROUTER_RUNNER.values()){
			enumMap.put(n.toString(), n);
		}
	}
	
	public static ROUTER_RUNNER getEnum(String key){
		return enumMap.get(key);
	}
}
