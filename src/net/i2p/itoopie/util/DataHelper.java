package net.i2p.itoopie.util;

import net.i2p.itoopie.i18n.Transl;

public class DataHelper {

	
    /**
     * Like formatDuration but with a non-breaking space after the number,
     * 0 is unitless, and the unit is translated.
     * This seems consistent with most style guides out there.
     * Thresholds are a little lower than in formatDuration() also,
     * as precision is less important in the GUI than in logging.
     */
    public static String formatDuration(long ms) {
        String t;
        if (ms == 0) {
            return "0";
        } else if (ms < 3 * 1000) {
            // milliseconds
            // Note to translators, may be negative or zero, 2999 maximum.
            // {0,number,####} prevents 1234 from being output as 1,234 in the English locale.
            // If you want the digit separator in your locale, translate as {0}.
           return Transl._("1 ms", "{0,number,####} ms", (int) ms);
        } else if (ms < 2 * 60 * 1000) {
            // seconds
            return Transl._("1 sec", "{0} sec", (int) (ms / 1000));
        } else if (ms < 120 * 60 * 1000) {
            // minutes
           return Transl._("1 min", "{0} min", (int) (ms / (60 * 1000)));
        } else if (ms < 2 * 24 * 60 * 60 * 1000) {
            // hours
            return Transl._("1 hour", "{0} hours", (int) (ms / (60 * 60 * 1000)));
        } else if (ms > 1000l * 24l * 60l * 60l * 1000l) {
        	// >1000 days
            return Transl._("n/a");
        } else {
            // days
            return Transl._("1 day", "{0} days", (int) (ms / (24 * 60 * 60 * 1000)));
        }
    }
}
