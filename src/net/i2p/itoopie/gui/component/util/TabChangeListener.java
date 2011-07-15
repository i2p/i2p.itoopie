package net.i2p.itoopie.gui.component.util;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.i2p.itoopie.gui.component.TabLogoPanel;


/**
 * Allows for tabs to be registered and notified when selected.
 * @author hottuna
 *
 */
public class TabChangeListener implements ChangeListener {

    private TabLogoPanel adaptee;
    
    /**
     * Register a TabLogoPanel to be notified when parent JTabbedPane fires sateChanged
     * @param adaptee
     */
    public TabChangeListener(TabLogoPanel adaptee) {
        this.adaptee = adaptee;
    }

    /**
     * Fire onTabFocus for visible tabs when stateChanged.
     */
    public void stateChanged(ChangeEvent e) {
        if (adaptee.isVisible()){
        	adaptee.onTabFocus(e);
        }
    }
}
