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
		VERSION { 				public boolean isReadable(){ return true;}
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.version"; }},
						
		UPTIME { 				public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.uptime"; }},
						
		STATUS { 				public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.status"; }},
						
		NETWORK_STATUS {		public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.net.status"; }},
		
		BW_INBOUND_1S {			public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.net.bw.inbound.1s"; }},
							
		BW_INBOUND_15S {		public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.net.bw.inbound.15s"; }},
		
		BW_OUTBOUND_1S {		public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.net.bw.outbound.1s"; }},
							
		BW_OUTBOUND_15S {		public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.net.bw.outbound.15s"; }},
		
		TUNNELS_PARTICIPATING {	public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.net.tunnels.participating"; }},
					
		KNOWN_PEERS {			public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.netdb.knownpeers"; }},
		
		ACTIVE_PEERS {			public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.netdb.activepeers"; }},
							
		FAST_PEERS {			public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.netdb.fastpeers"; }},
		
		HIGH_CAPACITY_PEERS {	public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.netdb.highcapacitypeers"; }},
								
		IS_RESEEDING {			public boolean isReadable(){ return true;}	
								public boolean isWritable(){ return false;}  
								public String toString() { return "i2p.router.netdb.isreseeding"; }}													
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
