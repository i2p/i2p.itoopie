package net.i2p.itoopie;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import net.i2p.itoopie.i18n.ItoopieTranslator;
import net.i2p.itoopie.util.BrowseException;
import net.i2p.itoopie.util.I2PDesktop;

/**
 * Manages the tray icon life.
 */
public class TrayManager {

    private static TrayManager instance = null;
    ///The tray area, or null if unsupported
    protected SystemTray tray = null;
    ///Our tray icon, or null if unsupported
    protected TrayIcon trayIcon = null;
    
    /**
     * Instantiate tray manager.
     */
    protected TrayManager() {}
    
    protected static synchronized TrayManager getInstance() {
        if(instance == null) {
               instance = new TrayManager();
        }
        return instance;
    }

    /**
     * Add the tray icon to the system tray and start everything up.
     */
    protected void startManager() {
        if(SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(getTrayImage(), "I2P", getMainMenu());
            trayIcon.setImageAutoSize(true); //Resize image to fit the system tray
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
            }
        }
    }
    
    protected void languageChanged() {
        trayIcon.setPopupMenu(getMainMenu());
    }

    
    /**
     * Get tray icon image from the itoopie resources in the jar file.
     * @return image used for the tray icon
     */
    private Image getTrayImage() {
        URL url = getClass().getResource("/resources/images/itoopie.png");
        System.out.println("URL"+url.getFile());
        //Image image = Toolkit.getDefaultToolkit().getImage(url);
        Image image = Toolkit.getDefaultToolkit().getImage("resources/images/itoopie.png");
        return image;
    }
    
    protected static String _(String s) {
        return ItoopieTranslator._(s);
    }
    
    
    /**
     * Build a popup menu, adding callbacks to the different items.
     * @return popup menu
     */
    public PopupMenu getMainMenu() {
        PopupMenu popup = new PopupMenu();
        
        MenuItem browserLauncher = new MenuItem(_("Launch I2P Browser"));
        browserLauncher.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                new SwingWorker<Object, Object>() {

                    @Override
                    protected Object doInBackground() throws Exception {
                        return null;
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            I2PDesktop.browse("http://localhost:7657");
                        } catch (BrowseException e1) {
                            //log.log(Log.WARN, "Failed to open browser!", e1);
                        }    
                    }
                    
                }.execute();
            }
        });
        MenuItem restartItem = new MenuItem(_("Restart I2P"));
        restartItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new SwingWorker<Object, Object>() {

                    @Override
                    protected Object doInBackground() throws Exception {
                        //RouterManager.restart();
                        return null;
                    }
                    
                }.execute();
                
            }
            
        });
        MenuItem stopItem = new MenuItem(_("Stop I2P"));
        stopItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new SwingWorker<Object, Object>() {
                    
                    @Override
                    protected Object doInBackground() throws Exception {
                        //RouterManager.shutDown();
                        return null;
                    }
                    
                }.execute();
                
            }
            
        });
        
        popup.add(browserLauncher);
        popup.addSeparator();
        popup.add(restartItem);
        popup.add(stopItem);
        
        return popup;
    }
}
