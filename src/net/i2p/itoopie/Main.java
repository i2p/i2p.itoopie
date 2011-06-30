package net.i2p.itoopie;

/*
 * Main.java
 */

import java.security.Security;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;

import net.i2p.itoopie.i2pcontrol.JSONInterface;
import net.i2p.itoopie.util.ConfigurationManager;

/**
 * The main class of the application.
 */
public class Main {
    
    ///Manages the lifetime of the tray icon.
    private TrayManager trayManager = null;
    private static ConfigurationManager _conf;
    private static Log _log;

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
        
        _conf.parseConfigStr("server-name=localhost");
        _conf.parseConfigStr("server-port=7656");
        _conf.parseConfigStr("server-target=jsonrpc");
        
        
        for (java.security.Provider p : Security.getProviders()){
        	System.out.println("Provider: " + p.getName());
        }
        for (String p : Security.getAlgorithms("KeyStore")){
        	System.out.println("KeyStore algorithm: " + p);
        }
        for (String p : Security.getAlgorithms("Cipher")){
        	System.out.println("Cipher algorithm: " + p);
        }
        
        String str = null;
		try {
			str = JSONInterface.getEcho("Echo this mofo!");
		} catch (JSONRPC2Error e) {
			_log.debug("getEcho Echo this mofo! failed.", e);
		}
		System.out.println("Echo response: " + str);
		
		
        Double dbl = null;
		try {
			dbl = JSONInterface.getRateStat("bw.sendRate", 3600000L);
		} catch (JSONRPC2Error e) {
			_log.debug("getRateStat(bw.sendRate, 3600000L) failed.", e);
		}
        System.out.println("rateStat: " + dbl);
        
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
