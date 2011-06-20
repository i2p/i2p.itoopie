package net.i2p.itoopie;

/*
 * Main.java
 */

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The main class of the application.
 */
public class Main {
    
    ///Manages the lifetime of the tray icon.
    private TrayManager trayManager = null;

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
        
        //ConfigurationManager.getInstance().loadArguments(args);
        
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
