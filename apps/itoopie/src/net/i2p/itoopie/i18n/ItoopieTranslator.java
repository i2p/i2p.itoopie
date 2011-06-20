package net.i2p.itoopie.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import net.i2p.itoopie.i18n.UTF8Control;


public class ItoopieTranslator {
    
    private static final String BUNDLE_NAME = "net.i2p.itoopie.messages";
     
    
    public static String _(String s) {
        return Translate.getString(s, BUNDLE_NAME);
    }

    public static String _(String s, Object o) {
        return Translate.getString(s, o, BUNDLE_NAME);
    }
	
}
