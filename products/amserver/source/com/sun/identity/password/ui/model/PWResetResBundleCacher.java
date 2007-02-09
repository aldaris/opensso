
package com.sun.identity.password.ui.model;

import com.iplanet.am.util.AMResourceBundleCache;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility to cache resource bundle object. It leverage on
 * <code>com.iplanet.am.util.AMResourceBundleCache</code>
 */
public class PWResetResBundleCacher {
    /**
     * Gets resource bundle
     *
     * @param name of bundle
     * @param locale of bundle
     * @return resource bundle
     */
    public static ResourceBundle getBundle(String name, Locale locale) {
	AMResourceBundleCache cache = AMResourceBundleCache.getInstance();
	ResourceBundle rb = cache.getResBundle(name, locale);

	if (rb == null) {
	    rb = cache.getResBundle(PWResetModel.DEFAULT_RB, locale);
	}

	return rb;
    }
}
