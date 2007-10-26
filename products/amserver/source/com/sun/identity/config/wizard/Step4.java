package com.sun.identity.config.wizard;

/**
 * @author Les Hazlewood
 */
public class Step4 extends LDAPStoreWizardPage {

    public static final String LDAP_STORE_SESSION_KEY = "wizardCustomUserStore";

    public Step4() {
        setType("user");
        setTypeTitle( "User" );
        setPageNum(4);
        setStoreSessionName( LDAP_STORE_SESSION_KEY );
    }
    
    public void onInit() {
        setPath("/config/wizard/step3.htm"); //uses the same template.  The rendered page is changed based on the above 3 props.
        super.onInit();
    }
}
