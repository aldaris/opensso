package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public abstract class AuthenticationStore {

    private Realm realm = new Realm();


    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

}
