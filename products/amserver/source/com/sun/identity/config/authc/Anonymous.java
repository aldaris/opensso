package com.sun.identity.config.authc;

import com.sun.identity.config.pojos.AnonymousStore;
import com.sun.identity.config.util.AjaxPage;

/**
 * @author Jeffrey Bermudez
 */
public class Anonymous extends AjaxPage {

    public AnonymousStore anonymousStore = null;

    public void onInit() {
        anonymousStore = new AnonymousStore();
    }

    public void onPost() {
        anonymousStore.getRealm().setName(toString("realmName"));
        anonymousStore.setAnonymousName(toString("anonymousName"));

        save(anonymousStore);
    }

    protected void save(AnonymousStore anonymousStore) {
        getConfigurator().addAuthenticationStore(anonymousStore);
    }

}