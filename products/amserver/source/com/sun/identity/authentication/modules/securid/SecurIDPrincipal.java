/**
 * $Id: SecurIDPrincipal.java,v 1.1 2008-08-29 22:06:39 kevinserwin Exp $
 * Copyright © 2005 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to
 * technology embodied in the product that is described in this document.
 * In particular, and without limitation, these intellectual property rights
 * may include one or more of the U.S. patents listed at
 * http://www.sun.com/patents and one or more additional patents or pending
 * patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and applicable
 * provisions of the FAR and its supplements.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.Sun,
 * Sun Microsystems and  the Sun logo are trademarks or registered trademarks
 * of Sun Microsystems, Inc. in the U.S. and other countries.  
 *
 * Copyright © 2005 Sun Microsystems, Inc. Tous droits réservés.
 * Sun Microsystems, Inc. détient les droits de propriété intellectuels relatifs
 * à la technologie incorporée dans le produit qui est décrit dans ce document.
 * En particulier, et ce sans limitation, ces droits de propriété
 * intellectuelle peuvent inclure un ou plus des brevets américains listés
 * à l'adresse http://www.sun.com/patents et un ou les brevets supplémentaires
 * ou les applications de brevet en attente aux Etats - Unis et dans les
 * autres pays.
 *
 * L'utilisation est soumise aux termes du contrat de licence.
 *
 * Cette distribution peut comprendre des composants développés par des
 * tierces parties.
 *
 * Sun,  Sun Microsystems et  le logo Sun sont des marques de fabrique ou des
 * marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
 * d'autres pays.
 */


package com.sun.identity.authentication.modules.securid;

import java.io.IOException;

import javax.security.auth.*;
import javax.security.auth.login.*;
import javax.security.auth.callback.*;

import java.security.Principal;


public class SecurIDPrincipal implements Principal, java.io.Serializable {

    /**
     * @serial
     */
    private String name;

    
    public SecurIDPrincipal(String name) {
	if (name == null)
	    throw new NullPointerException("illegal null input");

	this.name = name;
    }

    /**
     * Return the SecurID username for this <code>SecurIDPrincipal</code>.
     *
     * <p>
     *
     * @return the SecurID username for this <code>SecurIDPrincipal</code>
     */
    public String getName() {
	return name;
    }

    /**
     * Return a string representation of this <code>SecurIDPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this <code>SecurIDPrincipal</code>.
     */
    public String toString() {
	return("SecurIDPrincipal:  " + name);
    }

    /**
     * Compares the specified Object with this <code>SecurIDPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>SecurIDPrincipal</code> and the two SecurIDPrincipals
     * have the same username.
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *		<code>SecurIDPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *		<code>SecurIDPrincipal</code>.
     */
    public boolean equals(Object o) {
	if (o == null)
	    return false;

        if (this == o)
            return true;
 
        if (!(o instanceof SecurIDPrincipal))
            return false;
        SecurIDPrincipal that = (SecurIDPrincipal)o;

	if (this.getName().equals(that.getName()))
	    return true;
	return false;
    }
 
    /**
     * Return a hash code for this <code>SecurIDPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>SecurIDPrincipal</code>.
     */
    public int hashCode() {
	return name.hashCode();
    }
}
