package com.sun.identity.config.wizard;

import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class Step5 extends AjaxPage {

    public static final String LOAD_BALANCER_HOST_SESSION_KEY = "wizardLoadBalancerHostName";
    public static final String LOAD_BALANCER_PORT_SESSION_KEY = "wizardLoadBalancerPort";

    public ActionLink clearLink = new ActionLink("clear", this, "clear" );
    public ActionLink validateLink = new ActionLink("validate", this, "validate" );

    public Step5(){}

    public void onInit() {
        String host = (String)getContext().getSessionAttribute( LOAD_BALANCER_HOST_SESSION_KEY );
        Integer port = (Integer)getContext().getSessionAttribute( LOAD_BALANCER_PORT_SESSION_KEY );
        if ( host != null ) {
            addModel("host", host );
        }
        if ( port != null ) {
            addModel("port", port );
        }
    }

    public boolean clear() {
        getContext().removeSessionAttribute( LOAD_BALANCER_HOST_SESSION_KEY );
        getContext().removeSessionAttribute( LOAD_BALANCER_PORT_SESSION_KEY );
        setPath(null);
        return false;
    }

    public boolean validate() {
        String host = toString("host");
        if ( host == null ) {
            writeInvalid( "Please specify a host" );
        } else {
            int port = toInt("port");
            if ( port > 65535 ) {
                writeInvalid( "Please use a port less than or equal to 65535" );
            } else {
                try {
                    getConfigurator().testLoadBalancer( host, port );
                    getContext().setSessionAttribute( LOAD_BALANCER_HOST_SESSION_KEY, host );
                    getContext().setSessionAttribute( LOAD_BALANCER_PORT_SESSION_KEY, Integer.valueOf( port ) );
                    writeValid("Load Balancer found" );
                } catch ( Exception ex ) {
                    writeInvalid( ex.getMessage() );
                }
            }
        }

        setPath(null);
        return false;
    }

}
