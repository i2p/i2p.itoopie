package net.i2p.itoopie.util;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.net.URL;

public class IconLoader {
	
    /**
     * Get tray icon image from the itoopie resources in the jar file.
     * @return image used for the tray icon
     */
    public static Image getTrayImage() {
        
        // Assume square icons.
        int icoHeight = (int) SystemTray.getSystemTray().getTrayIconSize().getHeight();
        int desiredHeight;
        if	(icoHeight == 16 ||
        		icoHeight == 24 ||
        		icoHeight == 32 ||
        		icoHeight == 48 ||
        		icoHeight == 64 ||
        		icoHeight == 128 ||
        		icoHeight == 256 ||
        		icoHeight == 512){
        	desiredHeight = icoHeight;
        } else {
        	desiredHeight = 512;
        }
        
        if (IsJar.isRunningJar()){
        	Class classurl = (new IsJarTester()).getClass();
        	URL url = classurl.getResource("/resources/images/itoopie-"+desiredHeight+".png");
        	return Toolkit.getDefaultToolkit().getImage(url);
        } else {
        	return Toolkit.getDefaultToolkit().getImage("resources/images/itoopie-"+desiredHeight+".png");
        }
    }
    
    /**
     * Get tray icon image from the itoopie resources in the jar file.
     * @return image used for the tray icon
     */
    public static Image getIcon(String iconName, int icoHeight) {
        
        // Assume square icons.
        int desiredHeight;
        if	(icoHeight == 16 ||
        		icoHeight == 24 ||
        		icoHeight == 32 ||
        		icoHeight == 48 ||
        		icoHeight == 64 ||
        		icoHeight == 128 ||
        		icoHeight == 256 ||
        		icoHeight == 512){
        	desiredHeight = icoHeight;
        } else {
        	desiredHeight = 512;
        }
        
        if (IsJar.isRunningJar()){
        	Class classurl = (new IsJarTester()).getClass();
        	URL url = classurl.getResource("/resources/images/" + iconName + "-" + desiredHeight + ".png");
        	return Toolkit.getDefaultToolkit().getImage(url);
        } else {
        	return Toolkit.getDefaultToolkit().getImage("resources/images/" + iconName + "-" + desiredHeight + ".png");        
        }
    }
}
