package com.sun.identity.config.authc;

import com.sun.identity.config.util.AjaxPage;

import java.util.List;

/**
 * @author Jeffrey Bermudez
 */
public class RealmList extends AjaxPage {

    public List realms;

    public void onGet() {
        realms = getConfigurator().getRealms();
    }

}
