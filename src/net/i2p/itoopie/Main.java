package net.i2p.itoopie;

/*
 * Main.java
 */

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.TrayManager;
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import net.i2p.itoopie.i2pcontrol.methods.GetEcho;
import net.i2p.itoopie.i2pcontrol.methods.GetNetworkSetting;
import net.i2p.itoopie.i2pcontrol.methods.GetRateStat;
import net.i2p.itoopie.i2pcontrol.methods.NetworkSetting.NETWORK_SETTING;
import net.i2p.itoopie.i2pcontrol.methods.SetNetworkSetting;
import net.i2p.itoopie.security.CertificateHelper;

/**
 * The main class of the application.
 */
public class Main {
    
    ///Manages the lifetime of the tray icon.
    private TrayManager trayManager = null;
    private static ConfigurationManager _conf;
    private static Log _log;
    public static final boolean isDebug = true;

    /**
     * Start the tray icon code (loads tray icon in the tray area).
     * @throws Exception 
     */
    public void startUp() throws Exception {
        trayManager = TrayManager.getInstance();
        trayManager.startManager();
    }
    
    public static void main(String[] args) {
        beginStartup(args);
    }

    /**
     * Main method launching the application.
     */
    public static void beginStartup(String[] args) {
        System.setProperty("java.awt.headless", "false");
        _conf = ConfigurationManager.getInstance();
        _log = LogFactory.getLog(Main.class);
        HttpsURLConnection.setDefaultHostnameVerifier(CertificateHelper.getHostnameVerifier());
        
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (UnsupportedLookAndFeelException ex) {
        }
        

        final Main main = new Main();
        main.launchForeverLoop();
        try {
			main.startUp();
		} catch (Exception e) {
			_log.error("Error during TrayManager launch.", e);
		}
        //testStuff(); // Delete Me
    }
    
    @SuppressWarnings("static-access")
	public static void beginShutdown(){
    	_conf.writeConfFile();
    	System.exit(0);
    }
    
    /**
     * Avoids the app terminating because no Window is opened anymore.
     * More info: http://java.sun.com/javase/6/docs/api/java/awt/doc-files/AWTThreadIssues.html#Autoshutdown
     */
    public void launchForeverLoop() {
       Runnable r = new Runnable() {
            public void run() {
                try {
                    Object o = new Object();
                    synchronized (o) {
                        o.wait();
                    }
                } catch (InterruptedException ie) {
                }
            }
        };
        Thread t = new Thread(r);
        t.setDaemon(false);
        t.start();
    }
    
    
    private static void testStuff(){
        _conf.parseConfigStr("server.hostname=127.0.0.1");
        _conf.parseConfigStr("server.port=5555");
        _conf.parseConfigStr("server.target=jsonrpc");
        
        
        // Test basic echo method
        System.out.println("GetEcho");
		try {
			String str = GetEcho.execute("Echo this mofo!");
			System.out.println("Echo response: " + str);
		}catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		}
		
		// Test reading a rateStat
		System.out.println("GetRateStat");
		try {
			Double dbl = GetRateStat.execute("bw.sendRate", 3600000L);
			System.out.println("rateStat: " + dbl);
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		} catch (InvalidParametersException e) {
			System.out.println("Bad parameters sent..");
		}
        
        // Test reading all settings
		System.out.println("GetNetworkSetting");
        try {
        	EnumMap<NETWORK_SETTING, Object> em = GetNetworkSetting.execute(NETWORK_SETTING.values());
			System.out.println("getNetworkInfo: All: ");
			Set<Entry<NETWORK_SETTING, Object>> set = em.entrySet();
			for (Entry e : set){
				System.out.println(e.getKey() +":"+ e.getValue());
			}
		} catch (InvalidPasswordException e1) {
			System.out.println("Invalid password..");
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		}

        
        
        // Test saving all settings
        System.out.println("SetNetworkSetting - fail");
        try { 
        	HashMap<NETWORK_SETTING, String> hm = new HashMap<NETWORK_SETTING,String>();
        	
        	List<NETWORK_SETTING> list = Arrays.asList(NETWORK_SETTING.values());
        	for (NETWORK_SETTING i : list){
        		hm.put(i, "66"); // 66 is an arbitrary number that should work for most fields.
        	}
        	EnumMap<NETWORK_SETTING, Object> nextHM= SetNetworkSetting.execute(hm);
        	System.out.println("setNetworkInfo: All: ");
        	Set<Entry<NETWORK_SETTING, Object>> set = nextHM.entrySet();
        	for (Entry e : set){
        		System.out.println(e.getKey() +":"+ e.getValue());
        	}
        } catch (InvalidPasswordException e){
        	//e.printStackTrace();
        } catch (JSONRPC2SessionException e){
        	//e.printStackTrace();
        	System.out.println("Connection failed..");
        } catch (InvalidParametersException e) {
        	System.out.println("Bad parameters sent..");
        }
        
        // Manually test saving all(?) settings
        System.out.println("SetNetworkSetting");
        try { 
        	HashMap<NETWORK_SETTING, String> hm = new HashMap<NETWORK_SETTING,String>();
        	hm.put(NETWORK_SETTING.BW_IN, "666");
        	hm.put(NETWORK_SETTING.BW_OUT, "666");
        	hm.put(NETWORK_SETTING.BW_SHARE, "66");
        	hm.put(NETWORK_SETTING.DETECTED_IP, "66.66.66.66");
        	hm.put(NETWORK_SETTING.LAPTOP_MODE, "true");
        	hm.put(NETWORK_SETTING.TCP_AUTOIP, "always");
        	hm.put(NETWORK_SETTING.TCP_HOSTNAME, "66.66.66.66");
        	hm.put(NETWORK_SETTING.TCP_PORT, "66");
        	hm.put(NETWORK_SETTING.UDP_AUTO_IP, "local,upnp,ssu");
        	hm.put(NETWORK_SETTING.UDP_HOSTNAME, "66.66.66.66");
        	hm.put(NETWORK_SETTING.UDP_PORT, "66");
        	hm.put(NETWORK_SETTING.UPNP, "true");
        	
        	EnumMap<NETWORK_SETTING, Object> nextHM= SetNetworkSetting.execute(hm);
        	System.out.println("setNetworkInfo: Manual: ");
        	Set<Entry<NETWORK_SETTING, Object>> set = nextHM.entrySet();
        	for (Entry e : set){
        		System.out.println(e.getKey() +":"+ e.getValue());
        	}
        } catch (InvalidPasswordException e){
        	//e.printStackTrace();
        } catch (JSONRPC2SessionException e){
        	//e.printStackTrace();
        	System.out.println("Connection failed..");
        } catch (InvalidParametersException e) {
        	System.out.println("Bad parameters sent..");
        }
    }
}
