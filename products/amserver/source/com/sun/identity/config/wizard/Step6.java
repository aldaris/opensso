package com.sun.identity.config.wizard;

import com.sun.identity.config.pojos.LDAPStore;
import net.sf.click.Page;

/**
 * @author Les Hazlewood
 */
public class Step6 extends Page {

    public Step6(){}

    public void onInit() {
        String newInstanceUrl = (String)getContext().getSessionAttribute( Step2.NEW_INSTANCE_URL_SESSION_KEY );
        add( "newInstanceUrl", newInstanceUrl );

        LDAPStore configStore = (LDAPStore)getContext().getSessionAttribute( Step3.LDAP_STORE_SESSION_KEY );
        add( "configStore", configStore );

        LDAPStore userStore = (LDAPStore)getContext().getSessionAttribute( Step4.LDAP_STORE_SESSION_KEY );
        add( "userStore", userStore );

        String loadBalancerHost = (String)getContext().getSessionAttribute( Step5.LOAD_BALANCER_HOST_SESSION_KEY );
        add( "loadBalancerHost", loadBalancerHost );
        Integer loadBalancerPort = (Integer)getContext().getSessionAttribute( Step5.LOAD_BALANCER_PORT_SESSION_KEY );
        add( "loadBalancerPort", loadBalancerPort );
    }

    protected void add( String key, Object value ) {
        if ( value != null ) {
            addModel( key, value );
        }
    }
}
