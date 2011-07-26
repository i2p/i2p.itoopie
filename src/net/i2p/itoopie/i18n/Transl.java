package net.i2p.itoopie.i18n;

public class Transl {
    
    private static final String BUNDLE_NAME = "net.i2p.itoopie.messages";
     
    
    public static String _(String s) {
        return Translate.getString(s, BUNDLE_NAME);
    }

    
    /**
     *  translate a string with a parameter
     *  This is a lot more expensive than getString(s, ctx), so use sparingly.
     *
     *  @param s string to be translated containing {0}
     *    The {0} will be replaced by the parameter.
     *    Single quotes must be doubled, i.e. ' -> '' in the string.
     *  @param o parameter, not translated.
     *    To tranlslate parameter also, use _("foo {0} bar", _("baz"))
     *    Do not double the single quotes in the parameter.
     *    Use autoboxing to call with ints, longs, floats, etc.
     */
    public static String _(String s, Object o) {
        return Translate.getString(s, o, BUNDLE_NAME);
    }
    
    /** for {0} and {1} */
    public static String _(String s, Object o, Object o2){
    	return Translate.getString(s, o, o2, BUNDLE_NAME);
    }
    
    /**
     *  Use GNU ngettext
     *  For .po file format see http://www.gnu.org/software/gettext/manual/gettext.html.gz#Translating-plural-forms
     *
     *  @param n how many
     *  @param s singluar string, optionally with {0} e.g. "one tunnel"
     *  @param p plural string optionally with {0} e.g. "{0} tunnels"
     */
    public static String getString(int n, String s, String p) {
    	return Translate.getString( n, s, p, BUNDLE_NAME);
    }
	
}
