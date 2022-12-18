package net.i2p.itoopie.i18n;

import gnu.gettext.GettextResource;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;




/**
 * Translate strings efficiently.
 * We don't include an English or default ResourceBundle, we simply check
 * for "en" and return the original string.
 * Support real-time language changing with the routerconsole.lang property.
 *
 * @author zzz, from a base generated by eclipse.
 * @since 0.7.9
 */
public abstract class Translate {
    private static final String _localeLang = Locale.getDefault().getLanguage();
    private static final Map<String, ResourceBundle> _bundles = new ConcurrentHashMap<String, ResourceBundle>(2);
    private static final ConcurrentHashMap<String, Boolean> _missing = new ConcurrentHashMap<String, Boolean>(2);
    /** use to look for untagged strings */
    private static final String TEST_LANG = "xx";
    private static final String TEST_STRING = "XXXX";

    /** lang in routerconsole.lang property, else current locale */
    public static String getString(String key, String bun) {
        String lang = Locale.getDefault().getLanguage();
        if (lang.equals("en"))
            return key;
        else if (lang.equals(TEST_LANG))
            return TEST_STRING;
        // shouldnt happen but dont dump the po headers if it does
        if (key.equals(""))
            return key;
        ResourceBundle bundle = findBundle(bun, lang);
        if (bundle == null)
            return key;
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
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
    public static String getString(String s, Object o, String bun) {
        String lang = Locale.getDefault().getLanguage();
        if (lang.equals(TEST_LANG))
            return TEST_STRING + '(' + o + ')' + TEST_STRING;
        String x = getString(s, bun);
        Object[] oArray = new Object[1];
        oArray[0] = o;
        try {
            MessageFormat fmt = new MessageFormat(x, new Locale(lang));
            return fmt.format(oArray, new StringBuffer(), null).toString();
        } catch (IllegalArgumentException iae) {
            System.err.println("Bad format: orig: \"" + s +
                               "\" trans: \"" + x +
                               "\" param: \"" + o +
                               "\" lang: " + lang);
            return "FIXME: " + x + ' ' + o;
        }
    }

    /** for {0} and {1} */
    public static String getString(String s, Object o, Object o2, String bun) {
        String lang = Locale.getDefault().getLanguage();
        if (lang.equals(TEST_LANG))
            return TEST_STRING + '(' + o + ',' + o2 + ')' + TEST_STRING;
        String x = getString(s, bun);
        Object[] oArray = new Object[2];
        oArray[0] = o;
        oArray[1] = o2;
        try {
            MessageFormat fmt = new MessageFormat(x, new Locale(lang));
            return fmt.format(oArray, new StringBuffer(), null).toString();
        } catch (IllegalArgumentException iae) {
            System.err.println("Bad format: orig: \"" + s +
                               "\" trans: \"" + x +
                               "\" param1: \"" + o +
                               "\" param2: \"" + o2 +
                               "\" lang: " + lang);
            return "FIXME: " + x + ' ' + o + ',' + o2;
        }
    }

    /**
     *  Use GNU ngettext
     *  For .po file format see http://www.gnu.org/software/gettext/manual/gettext.html.gz#Translating-plural-forms
     *
     *  @param n how many
     *  @param s singluar string, optionally with {0} e.g. "one tunnel"
     *  @param p plural string optionally with {0} e.g. "{0} tunnels"
     *  @since 0.7.14
     */
    public static String getString(int n, String s, String p, String bun) {
        String lang = Locale.getDefault().getLanguage();
        if (lang.equals(TEST_LANG))
            return TEST_STRING + '(' + n + ')' + TEST_STRING;
        ResourceBundle bundle = null;
        if (!lang.equals("en"))
            bundle = findBundle(bun, lang);
        String x;
        if (bundle == null)
            x = n == 1 ? s : p;
        else
            x = GettextResource.ngettext(bundle, s, p, n);
        Object[] oArray = new Object[1];
        oArray[0] = Integer.valueOf(n);
        try {
            MessageFormat fmt = new MessageFormat(x, new Locale(lang));
            return fmt.format(oArray, new StringBuffer(), null).toString();
        } catch (IllegalArgumentException iae) {
            System.err.println("Bad format: sing: \"" + s +
                           "\" plural: \"" + p +
                           "\" lang: " + lang);
            return "FIXME: " + s + ' ' + p + ',' + n;
        }
    }


    /** cache both found and not found for speed */
    private static ResourceBundle findBundle(String bun, String lang) {
        String key = bun + '-' + lang;
        ResourceBundle rv = _bundles.get(key);
        if (rv == null && !_missing.containsKey(key)) {
            try {
                // We must specify the class loader so that a webapp can find the bundle in the .war
                rv = ResourceBundle.getBundle(bun, new Locale(lang), Thread.currentThread().getContextClassLoader());
                if (rv != null)
                    _bundles.put(key, rv);
            } catch (MissingResourceException e) {
                _missing.put(key, true);
            }
        }
        return rv;
    }

    /**
     *  Clear the cache.
     *  Call this after adding new bundles to the classpath.
     *  @since 0.7.12
     */
    public static void clearCache() {
        _missing.clear();
    }
}
