package net.i2p.itoopie.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.i2p.itoopie.Main;
import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.util.IconLoader;
import net.i2p.itoopie.util.IsJar;

/**
 * Manages the tray icon life.
 */
public class TrayManager {

    ///The tray area, or null if unsupported
    protected SystemTray tray;
    ///Our tray icon, or null if unsupported
    protected TrayIcon trayIcon;
    private final net.i2p.itoopie.Main main;    
    private final ConfigurationManager _conf;
    
    /**
     * Instantiate tray manager.
     */
    public TrayManager(net.i2p.itoopie.Main m, ConfigurationManager conf) {
        main = m;
	_conf = conf;
    }
    
    /**
     * Add the tray icon to the system tray and start everything up.
     */
    public synchronized void startManager() {
	final WindowHandler windowHandler = new WindowHandler(_conf);
	windowHandler.toggleFrames();
	// so the tray icon works right on Gnome
	try { Thread.sleep(500); } catch (InterruptedException ie) {}
    	SwingUtilities.invokeLater(new Runnable(){
    		public void run(){
		        if(SystemTray.isSupported()) {
		            final Image img = IconLoader.getTrayImage();
		            tray = SystemTray.getSystemTray();
		            trayIcon = new TrayIcon(img, "itoopie", getMainMenu());
		            trayIcon.setImageAutoSize(true); //Resize image to fit the system tray
		            try {
		                tray.add(trayIcon);
		            } catch (AWTException e) { e.printStackTrace(); }
		            
		            trayIcon.addMouseListener(new MouseAdapter(){
						@Override
						public void mouseClicked(MouseEvent arg0) {
							windowHandler.toggleFrames();
						}
		            });
		        }
    		}
    	});
    }

    /**
     *  @since 0.0.5 for plugin only
     */
    public synchronized void stopManager() {
        try {
            if (trayIcon != null && SystemTray.isSupported()) {
                SystemTray.getSystemTray().remove(trayIcon);
                // TODO stop it
                trayIcon = null;
            }
        } catch (Exception e) {}
    }

    protected void languageChanged() {
        trayIcon.setPopupMenu(getMainMenu());
    }

    
    
    /**
     * Build a popup menu, adding callbacks to the different items.
     * @return popup menu
     */
    public PopupMenu getMainMenu() {
        PopupMenu popup = new PopupMenu();
        MenuItem stopItem = new MenuItem(Transl._t("Exit itoopie"));
        stopItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new SwingWorker<Object, Object>() {
                    
                    @Override
                    protected Object doInBackground() throws Exception {
                        main.beginShutdown();
                        return null;
                    }
                }.execute();
            }
        });
        
        popup.add(stopItem);
        return popup;
    }
}
