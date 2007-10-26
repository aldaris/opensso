package com.sun.identity.config.authc;

import com.sun.identity.config.util.AjaxPage;

/**
 * @author Jeffrey Bermudez
 */
public class DataStore extends AjaxPage {

    public com.sun.identity.config.pojos.DataStore dataStore = null;

    public void onInit() {
        dataStore = new com.sun.identity.config.pojos.DataStore();
    }

    public void onPost() {
        dataStore.getRealm().setName(toString("realmName"));

        save(dataStore);
    }

    protected void save(com.sun.identity.config.pojos.DataStore dataStore) {
        getConfigurator().addAuthenticationStore(dataStore);
    }

}