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


public class NetworkInfo{
	public final static HashMap<String,NETWORK_INFO> enumMap;

	
	/**
	 * Describes the most common network related settings and their API key.
	 * @author hottuna
	 */
	public enum NETWORK_INFO implements RemoteSetable{
		DETECTED_IP { 	public boolean isSetable(){ return false;} public String toString() { return "i2p.router.net.ssu.detectedip"; }},
		TCP_PORT { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ntcp.port"; }},
		TCP_HOSTNAME { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ntcp.hostname"; }},
		TCP_AUTOIP { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ntcp.autoip"; }},
		UDP_PORT { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ssu.port"; }},
		UDP_HOSTNAME { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ssu.hostname"; }},
		UDP_AUTO_IP { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ssu.autoip"; }},
		UPNP { 			public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.upnp"; }},
		BW_SHARE { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.bw.share"; }},
		BW_IN { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.bw.in"; }},
		BW_OUT { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.bw.out"; }},
		LAPTOP_MODE { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.laptopmode"; }}
		};
		
	static {
		enumMap = new HashMap<String,NETWORK_INFO>();
		for (NETWORK_INFO n : NETWORK_INFO.values()){
			enumMap.put(n.toString(), n);
		}
	}
}
