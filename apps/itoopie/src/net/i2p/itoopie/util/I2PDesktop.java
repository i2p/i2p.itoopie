package net.i2p.itoopie.util;

import java.awt.Desktop;
import java.awt.TrayIcon;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class I2PDesktop {
    
    
    public static void browse(String url) throws BrowseException {
        if(Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if(desktop.isSupported(Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                } catch (Exception e) {
                    throw new BrowseException();
                }
            }
            else {
                throw new BrowseException();
            }
        }
        else {
            throw new BrowseException();
        }
    }
}