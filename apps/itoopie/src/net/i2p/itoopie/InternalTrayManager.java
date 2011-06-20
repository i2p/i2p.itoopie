package net.i2p.itoopie;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingWorker;

import net.i2p.itoopie.util.BrowseException;
import net.i2p.itoopie.util.I2PDesktop;

public class InternalTrayManager extends TrayManager {
	

    protected InternalTrayManager() {}

    @Override
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
