package com.sun.identity.config.authc;

import com.sun.identity.config.pojos.HTTPStore;
import com.sun.identity.config.util.AjaxPage;

/**
 * @author Jeffrey Bermudez
 */
public class HTTP extends AjaxPage {

    public HTTPStore httpStore = null;

    public void onInit() {
        httpStore = new HTTPStore();
    }

    public void onPost() {
        httpStore.getRealm().setName(toString("realmName"));

        save(httpStore);
    }

    protected void save(HTTPStore httpStore) {
        getConfigurator().addAuthenticationStore(httpStore);
    }
    
}