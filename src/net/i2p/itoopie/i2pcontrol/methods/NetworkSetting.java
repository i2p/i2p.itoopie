package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;


/*
 * i2p.router.net.ntcp.port
 * i2p.router.net.ntcp.hostname
 * i2p.router.net.ntcp.autoip // true|always|false //disables autodetect|disabled //disables ntcp
 * i2p.router.net.ssu.port
 * i2p.router.net.ssu.hostname
 * i2p.router.net.ssu.detectedip
 * i2p.router.net.ssu.autoip //[local,upnp,ssu] any of prev., in order |fixed // fixed = no detection
 * i2p.router.net.upnp //
 * i2p.router.net.bw.share
 * i2p.router.net.bw.in
 * i2p.router.net.bw.out
 * i2p.router.net.laptopmode
 */


public class NetworkSetting{
	private final static HashMap<String,NETWORK_SETTING> enumMap;

	
	/**
	 * Describes the most common network related settings and their API key.
	 * @author hottuna
	 */
	public enum NETWORK_SETTING implements Remote{
		DETECTED_IP { 	public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return false;} 
						public String toString() { return "i2p.router.net.ssu.detectedip"; }},
						
		TCP_PORT { 		public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.ntcp.port"; }},
						
		TCP_HOSTNAME { 	public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.ntcp.hostname"; }},
						
		TCP_AUTOIP { 	public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.ntcp.autoip"; }},
						
		UDP_PORT { 		public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.ssu.port"; }},
						
		UDP_HOSTNAME { 	public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.ssu.hostname"; }},
						
		UDP_AUTO_IP { 	public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.ssu.autoip"; }},
						
		UPNP { 			public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.upnp"; }},
						
		BW_SHARE { 		public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.bw.share"; }},
						
		BW_IN { 		public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.bw.in"; }},
						
		BW_OUT { 		public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.bw.out"; }},
						
		LAPTOP_MODE { 	public boolean isReadable(){ return true;}	
						public boolean isWritable(){ return true;}  
						public String toString() { return "i2p.router.net.laptopmode"; }},
						
		SETTINGS_SAVED {public boolean isReadable(){ return false;}	
						public boolean isWritable(){ return false;} 
						public String toString() { return "SettingsSaved";}},
						
		RESTART_NEEDED {public boolean isReadable(){ return false;}	
						public boolean isWritable(){ return false;} 
						public String toString() { return "RestartNeeded";}}
	};
		
	static {
		enumMap = new HashMap<String,NETWORK_SETTING>();
		for (NETWORK_SETTING n : NETWORK_SETTING.values()){
			enumMap.put(n.toString(), n);
		}
	}
	
	public static NETWORK_SETTING getEnum(String key){
		return enumMap.get(key);
	}
}
