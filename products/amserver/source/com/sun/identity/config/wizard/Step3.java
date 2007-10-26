package com.sun.identity.config.wizard;

/**
 * @author Les Hazlewood
 */
public class Step3 extends LDAPStoreWizardPage {

    public static final String LDAP_STORE_SESSION_KEY = "wizardCustomConfigStore";

    public Step3() {
        setType("config");
        setTypeTitle( "Configuration" );
        setPageNum(3);
        setStoreSessionName( LDAP_STORE_SESSION_KEY );
    }
}
