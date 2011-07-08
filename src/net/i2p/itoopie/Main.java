package net.i2p.itoopie;

/*
 * Main.java
 */

import java.util.Arrays;
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
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONInterface;
import net.i2p.itoopie.i2pcontrol.JSONInterface.NETWORK_INFO;
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
        /*
        if(RouterManager.inI2P()) {
            RouterManager.getRouterContext().addPropertyCallback(new I2PPropertyCallback() {

                @Override
                public void propertyChanged(String arg0, String arg1) {
                    if(arg0.equals(Translate.PROP_LANG)) {
                        trayManager.languageChanged();
                    }
                }
                
            });
        }
        */
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
            //log.log(Log.ERROR, null, ex);
        } catch (InstantiationException ex) {
            //log.log(Log.ERROR, null, ex);
        } catch (IllegalAccessException ex) {
            //log.log(Log.ERROR, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            //log.log(Log.ERROR, null, ex);
        }
        
        _conf.parseConfigStr("server.hostname=127.0.0.1");
        _conf.parseConfigStr("server.port=5555");
        _conf.parseConfigStr("server.target=jsonrpc");
        
        
        // Test basic echo method
		try {
			String str = JSONInterface.getEcho("Echo this mofo!");
			System.out.println("Echo response: " + str);
		}catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		}
		
		// Test reading a rateStat
		try {
			Double dbl = JSONInterface.getRateStat("bw.sendRate", 3600000L);
			System.out.println("rateStat: " + dbl);
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		} catch (InvalidParametersException e) {
			System.out.println("Bad parameters sent..");
		}
        
        // Test reading all settings
        try {
        	HashMap hm = JSONInterface.getNetworkInfo(JSONInterface.NETWORK_INFO.values());
			System.out.println("getNetworkInfo: All: ");
			Set<Entry> set = hm.entrySet();
			for (Entry e : set){
				System.out.println(e.getKey() +":"+ e.getValue());
			}
		} catch (InvalidPasswordException e1) {
			//e1.printStackTrace();
		} catch (JSONRPC2SessionException e) {
			System.out.println("Connection failed..");
		}

        
        
        // Test saving all settings
        try { 
        	HashMap<NETWORK_INFO, String> hm = new HashMap<NETWORK_INFO,String>();
        	
        	List<NETWORK_INFO> list = Arrays.asList(NETWORK_INFO.values());
        	for (NETWORK_INFO i : list){
        		hm.put(i, "66"); // 66 is an arbitrary number that should work for most fields.
        	}
        	HashMap nextHM= JSONInterface.setNetworkSetting(hm);
        	System.out.println("setNetworkInfo: All: ");
        	Set<Entry> set = nextHM.entrySet();
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
        try { 
        	HashMap<NETWORK_INFO, String> hm = new HashMap<NETWORK_INFO,String>();
        	hm.put(NETWORK_INFO.BW_IN, "666");
        	hm.put(NETWORK_INFO.BW_OUT, "666");
        	hm.put(NETWORK_INFO.BW_SHARE, "66");
        	hm.put(NETWORK_INFO.DETECTED_IP, "66.66.66.66");
        	hm.put(NETWORK_INFO.LAPTOP_MODE, "true");
        	hm.put(NETWORK_INFO.TCP_AUTOIP, "always");
        	hm.put(NETWORK_INFO.TCP_HOSTNAME, "66.66.66.66");
        	hm.put(NETWORK_INFO.TCP_PORT, "66");
        	hm.put(NETWORK_INFO.UDP_AUTO_IP, "local,upnp,ssu");
        	hm.put(NETWORK_INFO.UDP_HOSTNAME, "66.66.66.66");
        	hm.put(NETWORK_INFO.UDP_PORT, "66");
        	hm.put(NETWORK_INFO.UPNP, "true");
        	
        	HashMap nextHM= JSONInterface.setNetworkSetting(hm);
        	System.out.println("setNetworkInfo: Manual: ");
        	Set<Entry> set = nextHM.entrySet();
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
        final Main main = new Main();
        main.launchForeverLoop();
        //We'll be doing GUI work, so let's stay in the event dispatcher thread.
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    main.startUp();
                }
                catch(Exception e) {
                    //log.error("Failed while running desktopgui!", e);
                }
                
            }
            
        });
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
    
}
