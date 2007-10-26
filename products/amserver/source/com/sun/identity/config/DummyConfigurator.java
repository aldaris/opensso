package com.sun.identity.config;

import com.sun.identity.config.pojos.*;
import net.sf.click.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Just a dummy class for testing.
 *
 * @author Les Hazlewood
 */
public class DummyConfigurator implements Configurator {

    public static final String PASSWORD_SET_KEY = "passwordSet";

    private Page page = null;

    public DummyConfigurator( Page page ) {
        this.page = page;
    }

    public boolean isNewInstall() {
        //for now simulate with url param - if it doesn't exist, assume new install, if it does, assume upgrade:
        return page.getContext().getRequest().getParameter( "upgrade" ) == null;
    }

    public boolean isPasswordUpdateRequired() {
        return page.getContext().getSessionAttribute( PASSWORD_SET_KEY ) == null;
    }

    public void setPassword( String username, String password ) {
        //simulate call w/ back end for now:
        page.getContext().setSessionAttribute( PASSWORD_SET_KEY, "true" );
    }

    public void testHost( LDAPStore store ) {
    }

    public void testBaseDN( LDAPStore store ) {
    }

    public void testLoginId( LDAPStore store ) {
    }

    public void testLoadBalancer( String host, int port ) {
    }

    public void writeConfiguration() {
    }

    public void writeConfiguration( String newInstanceUrl, LDAPStore configStor, LDAPStore userStore, String loadBalancerHost, int loadBalancerPort ) {
    }

    public List getExistingConfigurations() {
        List existing = new ArrayList(3);
        existing.add( "http://fam.company.com:8080/fam/");
        existing.add( "http://fam.sun.com:8080/fam/");
        existing.add( "http://some.other.server.com:8080/opensso/" );
        return existing;
    }

    public void writeConfiguration( List configStringUrls ) {
    }

    public void testNewInstanceUrl( String url ) {
    }

    public void pushConfiguration( String instanceUrl ) {
    }

    public void upgrade() {
    }

    public void coexist() {
    }

    public void olderUpgrade() {
    }

    public List getRealms() {
        List realms = new ArrayList();
        for (int i = 0; i < 8; i++) {
            Realm realm = new Realm();
            realm.setName("Realm_" + (i + 1));
            realms.add(realm);
        }
        return realms;
    }


    public List getUsers(Realm realm, String filter) {
        List users = new ArrayList();

        for (int i = 0; i < 500; i++) {
            RealmUser realmUser = new RealmUser();
            realmUser.setFirstName("FirstName" + (i + 1));
            realmUser.setLastName("LastName" + (i + 1));
            users.add(realmUser);
        }

        return users;
    }

    public List getAdministrators(Realm realm, RealmRole role) {
        List realmAdmins = new ArrayList();

        for (int i = 0; i < 8; i++) {
            RealmUser realmUser = new RealmUser();
            realmUser.setFirstName("FirstName" + (i + 1));
            realmUser.setLastName("LastName" + (i + 1));
            realmUser.setRealmRole(new RealmRole());
            realmAdmins.add(realmUser);
        }

        return realmAdmins;
    }

    public void addAuthenticationStore(AuthenticationStore authenticationStore) {
        page.getContext().setSessionAttribute("AuthenticationStore", authenticationStore);        
    }

}
