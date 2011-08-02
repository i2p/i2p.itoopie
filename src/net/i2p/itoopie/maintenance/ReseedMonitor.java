package net.i2p.itoopie.maintenance;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.methods.GetRouterInfo;
import net.i2p.itoopie.i2pcontrol.methods.RouterInfo.ROUTER_INFO;
import net.i2p.itoopie.i2pcontrol.methods.RouterManager.ROUTER_MANAGER;
import net.i2p.itoopie.i2pcontrol.methods.SetRouterManager;
import net.i2p.itoopie.security.ItoopieHostnameVerifier;

/*
	timer = new Timer();
	// Start running periodic task after 20 minutes, run periodically every 10th minute.
	timer.scheduleAtFixedRate(new Sweeper(), 1000*60*20, 1000*60*10);
 */

/**
 * Monitors the amount of peers the remote I2P-node has and initiates a reseed if deemed needed.
 * @author hottuna
 *
 */
public class ReseedMonitor extends TimerTask{
	private static final Long MIN_KNOWN_PEERS = new Long(30);
	private static final Log _log;
	
	static {
		_log = LogFactory.getLog(ReseedMonitor.class);
	}

	@Override
	public void run(){
		try {
			EnumMap<ROUTER_INFO, Object> em = GetRouterInfo.execute(ROUTER_INFO.KNOWN_PEERS, ROUTER_INFO.IS_RESEEDING);
			Long knownPeers = (Long) em.get(ROUTER_INFO.KNOWN_PEERS);
			Boolean isReseeding = (Boolean) em.get(ROUTER_INFO.IS_RESEEDING);
			if (knownPeers != null && isReseeding != null){
				if (knownPeers < MIN_KNOWN_PEERS){
					_log.info("Few peers detected, initiating reseed..");
					HashMap<ROUTER_MANAGER, String> hm = new HashMap<ROUTER_MANAGER, String>();
					hm.put(ROUTER_MANAGER.RESEED, null);
					SetRouterManager.execute(hm);
				}
			}
		} catch (InvalidPasswordException e) {
			_log.error("Password denied by remote I2PControl host.");
		} catch (JSONRPC2SessionException e) {
			_log.error("Error connecting to remote I2PControl host.");
		}
		
	}
	
	public static void main(String[] args){
		System.out.println("Reading config file..");
		ConfigurationManager _conf = ConfigurationManager.getInstance();
        HttpsURLConnection.setDefaultHostnameVerifier(new ItoopieHostnameVerifier());
        _conf.parseConfigStr("server.hostname=127.0.0.1");
        _conf.parseConfigStr("server.port=7650");
        _conf.parseConfigStr("server.target=jsonrpc");
        
		
		try {
			EnumMap<ROUTER_INFO, Object> em = GetRouterInfo.execute(ROUTER_INFO.KNOWN_PEERS, ROUTER_INFO.IS_RESEEDING);
			Long knownPeers = (Long) em.get(ROUTER_INFO.KNOWN_PEERS);
			Boolean isReseeding = (Boolean) em.get(ROUTER_INFO.IS_RESEEDING);
			System.out.println("Known peers: " + knownPeers);
			System.out.println("Is reseeding: " + isReseeding);
			
			System.out.println("Initiating reseed...");
			HashMap<ROUTER_MANAGER, String> hm = new HashMap<ROUTER_MANAGER, String>();
			hm.put(ROUTER_MANAGER.RESEED, null);
			SetRouterManager.execute(hm);
			System.out.println("Waiting...");
			Thread.sleep(1000);
			System.out.println("Initiating second reseed...");
			hm = new HashMap<ROUTER_MANAGER, String>();
			hm.put(ROUTER_MANAGER.RESEED, null);
			SetRouterManager.execute(hm);
			System.out.println("Waiting...");
			Thread.sleep(1000);
			em = GetRouterInfo.execute(ROUTER_INFO.KNOWN_PEERS, ROUTER_INFO.IS_RESEEDING);
			isReseeding = (Boolean) em.get(ROUTER_INFO.IS_RESEEDING);
			System.out.println("Is reseeding: " + isReseeding);

			
			if (knownPeers != null && isReseeding != null){
				if (knownPeers < MIN_KNOWN_PEERS){
				}
			}
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
