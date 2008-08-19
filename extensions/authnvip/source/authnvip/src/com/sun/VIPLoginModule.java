/**
 * A Sun Java System Access Manager Custom Authentication Module
 * Based upon the SocketLoginModule at http://wikis.sun.com/
 * Contributors: Terry J. Gardner, Jeff Bounds
 */
package com.sun;


import com.sun.identity.authentication.spi.AMLoginModule;
import com.sun.identity.authentication.spi.AuthLoginException;




import java.util.Map;

import javax.security.auth.*;
import javax.security.auth.callback.*;

/**
 * @author Terry J. Gardner
 */
public class VIPLoginModule extends AMLoginModule {

    //------------ public ------------
    
    /**
     * initialize this object
     *
     * @param subject
     * @param sharedState
     * @param options
     */
    @Override
    public void init(Subject subject, Map sharedState, Map options) {
	// no implementation necessary
    }
    
    /**
     * This method does the authentication of the subject
     *
     * @param callbacks           the array of callbacks from the module configuration file
     * @param state the           current state of the authentication process
     * @throws AuthLoginException if an error occurs
     */
    @Override
    public int process(Callback[] callbacks,int state) throws AuthLoginException {
        
	// this module is married to the module properties file
	// therefore the number of callbacks must match
	if(callbacks.length < 3) {
	    throw new AuthLoginException("fatal configuration error, wrong number of callbacks");
	}

        int currentState = state;

        if(currentState == 1) {

	    // get the username
	    userName = ((NameCallback)callbacks[0]).getName();
            if(userName == null || userName.equals("")) {
                throw new AuthLoginException("username cannot be empty");
            }
            
            vipTokenId = ((NameCallback)callbacks[1]).getName();
            if(vipTokenId == null || vipTokenId.equals("")) {
                throw new AuthLoginException("Token ID cannot be empty");
            }

            otp = ((NameCallback)callbacks[2]).getName();
            if(otp == null || otp.equals("")) {
                throw new AuthLoginException("OTP cannot be empty");
            }
            
            //Create the VIPWebServiceClient.   We pass in the userName, tokenID, and otp
            //Here is where we would use the username to determine the tokenID.

	    VIPWebServiceClient vipWSC = new VIPWebServiceClient(userName,vipTokenId,otp);
	    String response = null;
	    
	    try {
		response = vipWSC.validateToken();            
	    } catch(Exception ex) {
		throw new AuthLoginException("Exception receiving response");
	    }
	    	    
	    // check the response from the peer
	    if(response == null) {
		throw new AuthLoginException("null response from authenticator system");
	    } else if(!vipWSC.isOK()) {
		throw new AuthLoginException("login failure");
	    }
            ++currentState;
	    // this login module only has one state, though

	    // save the user name. getPrincipal()
	    // will use the userTokenID to return the
	    // Principal object. <code>getPrincipal</code>
	    // should return the last good authentication
	    userTokenId = userName;
		
        }
        return -1; // -1 indicates success
    }

    /**
     * return the Principal object,
     * creating it if necessary. This method
     * is invoked at the end of successful
     * authentication session. relies on
     * userTokenID being set by process()
     *
     * <p>
     *
     * @return the Principal object or null if userTokenId is null
     */
    @Override
    public java.security.Principal getPrincipal() {
        
        java.security.Principal thePrincipal = null;
        if(userPrincipal != null) {
            thePrincipal = userPrincipal;
        } else if(userTokenId != null) {
            userPrincipal = new VIPPrincipal(userName);
            thePrincipal = userPrincipal;
        }
        return thePrincipal;
        
    }
    
    // ------------ private ------------
    
    private java.security.Principal userPrincipal = null;
    private String userTokenId;
    private String userName;
    private String vipTokenId;
    private String otp;
    
}

