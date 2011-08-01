package net.i2p.itoopie;

/*
 * Main.java
 */


import java.util.EnumMap;
import java.util.Timer;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.gui.GUIHelper;
import net.i2p.itoopie.gui.TrayManager;
import net.i2p.itoopie.gui.WindowHandler;
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import net.i2p.itoopie.i2pcontrol.methods.GetEcho;
import net.i2p.itoopie.i2pcontrol.methods.GetNetworkSetting;
import net.i2p.itoopie.i2pcontrol.methods.GetRateStat;
import net.i2p.itoopie.i2pcontrol.methods.GetRouterInfo;
import net.i2p.itoopie.i2pcontrol.methods.I2PControl.I2P_CONTROL;
import net.i2p.itoopie.i2pcontrol.methods.NetworkSetting.NETWORK_SETTING;
import net.i2p.itoopie.i2pcontrol.methods.RouterInfo.ROUTER_INFO;
import net.i2p.itoopie.i2pcontrol.methods.RouterManager.ROUTER_MANAGER;
import net.i2p.itoopie.i2pcontrol.methods.SetI2PControl;
import net.i2p.itoopie.i2pcontrol.methods.SetNetworkSetting;
import net.i2p.itoopie.i2pcontrol.methods.SetRouterManager;
import net.i2p.itoopie.maintenance.ReseedMonitor;
import net.i2p.itoopie.security.ItoopieHostnameVerifier;

/**
 * The main class of the application.
 */
public class Main {
    
    ///Manages the lifetime of the tray icon.
    private TrayManager trayManager = null;
    private static ConfigurationManager _conf;
    private static Timer reseedMonitor;
    private static Log _log;

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
        HttpsURLConnection.setDefaultHostnameVerifier(new ItoopieHostnameVerifier());
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            GUIHelper.setDefaultStyle();
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
        
        // Popup Main window.
        WindowHandler.toggleFrames();
        
        
		reseedMonitor = new Timer();
		// Start running periodic task after 2 minutes, run periodically every 10th minute.
		reseedMonitor.scheduleAtFixedRate(new ReseedMonitor(), 2*60*1000, 10*60*1000);

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
        
        
        // Try port switching
        /*
        System.out.println("\nI2PControl - Port Switch");
        try {
	        HashMap<I2P_CONTROL, String> hm = new HashMap<I2P_CONTROL, String>();
	        hm.put(I2P_CONTROL.PORT, 7888+"");
	        SetI2PControl.execute(hm);
	        _conf.setConf("server.port", 7888);
	        Thread.sleep(10*1000);
        } catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		} catch (InvalidParametersException e) {
			System.out.println("Bad parameters sent..");
		} catch (InterruptedException e) {	}
        
        
        // Try passwd switching
        System.out.println("\nI2PControl - Password Switch");
        try {
	        HashMap<I2P_CONTROL, String> hm = new HashMap<I2P_CONTROL, String>();
	        hm.put(I2P_CONTROL.PASSWORD, "itoopi");
	        SetI2PControl.execute(hm);
	        _conf.setConf("server.password", "itoopi");
        } catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		} catch (InvalidParametersException e) {
			System.out.println("Bad parameters sent..");
		}
        
        
        // Test basic echo method
        System.out.println("\nGetEcho");
		try {
			String str = GetEcho.execute("Echo this mofo!");
			System.out.println("Echo response: " + str);
		}catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		}
		
		// Test reading a rateStat
		System.out.println("\nGetRateStat");
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
		System.out.println("\nGetNetworkSetting");
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
        System.out.println("\nSetNetworkSetting - fail");
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
        System.out.println("\nSetNetworkSetting");
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
        
        */
        // Test reading all router info
		System.out.println("\nGetRouterInfo");
        try {
        	EnumMap<ROUTER_INFO, Object> em = GetRouterInfo.execute(ROUTER_INFO.values());
			System.out.println("getNetworkInfo: All: ");
			Set<Entry<ROUTER_INFO, Object>> set = em.entrySet();
			for (Entry e : set){
				System.out.println(e.getKey() +":"+ e.getValue());
			}
		} catch (InvalidPasswordException e1) {
			System.out.println("Invalid password..");
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
			e.printStackTrace();
		}

        
        // Test restart - worked at one point :) Possibly now as well.
        /*
        System.out.println("\nSetRouterRunner: Restart");
        try {
			SetRouterRunner.execute(ROUTER_RUNNER.RESTART);
		} catch (InvalidPasswordException e1) {
			System.out.println("Invalid password..");
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		}*/
        
        // Test restart graceful - worked at one point :) Possibly now as well.
        /*
        System.out.println("\nSetRouterRunner: Restart Graceful");
        try {
			SetRouterRunner.execute(ROUTER_RUNNER.RESTART_GRACEFUL);
		} catch (InvalidPasswordException e1) {
			System.out.println("Invalid password..");
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		}*/
        
        /*
		// Test shutdown - worked at one point :) Possibly now as well.
        System.out.println("\nSetRouterRunner: Shutdown ");
        try {
			SetRouterRunner.execute(ROUTER_RUNNER.SHUTDOWN);
		} catch (InvalidPasswordException e1) {
			System.out.println("Invalid password..");
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		}
		*/
    }

}
