package com.sun.identity.config.authc;

import com.sun.identity.config.pojos.FederationStore;
import com.sun.identity.config.util.AjaxPage;

/**
 * @author Jeffrey Bermudez
 */
public class Federation extends AjaxPage {

    public FederationStore federationStore = null;

    public void onInit() {
        federationStore = new FederationStore();
    }

    public void onPost() {
        federationStore.getRealm().setName(toString("realmName"));

        save(federationStore);
    }

    protected void save(FederationStore federationStore) {
        getConfigurator().addAuthenticationStore(federationStore);
    }

}