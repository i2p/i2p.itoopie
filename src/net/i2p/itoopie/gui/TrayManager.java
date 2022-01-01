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
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.util.IconLoader;
import net.i2p.itoopie.util.IsJar;

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
    
    public static synchronized TrayManager getInstance() {
        if(instance == null) {
               instance = new TrayManager();
        }
        return instance;
    }

    /**
     * Add the tray icon to the system tray and start everything up.
     */
    public void startManager() {
    	SwingUtilities.invokeLater(new Runnable(){
    		public void run(){
		        if(SystemTray.isSupported()) {
		            tray = SystemTray.getSystemTray();
		            trayIcon = new TrayIcon(IconLoader.getTrayImage(), "itoopie", getMainMenu());
		            trayIcon.setImageAutoSize(true); //Resize image to fit the system tray
		            
		            trayIcon.addMouseListener(new MouseAdapter(){
						@Override
						public void mouseClicked(MouseEvent arg0) {
							WindowHandler.toggleFrames();
						}
		            });
		            try {
		                tray.add(trayIcon);
		            } catch (AWTException e) {}
		        }
    		}
    	});
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
                        Main.beginShutdown();
                        return null;
                    }
                }.execute();
            }
        });
        
        popup.add(stopItem);
        return popup;
    }
}
