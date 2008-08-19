/*
 * VIPPrincipal.java
 *
 * Contributors: Terry J. Gardner, Jeff Bounds
 */

package com.sun;

import java.security.Principal;

/**
 * Implements the methods in the Principal interface
 *
 * @author Terry J. Gardner
 */
public class VIPPrincipal implements Principal {
    
    /**
     * @serial
     */
    private String name;
    
    public VIPPrincipal(String name) {
        if(name == null) {
            throw new NullPointerException("illegal null input");
        }
        this.name = name;
    }
    
    /**
     * Return the LDAP username for this <code>VIPPrincipal</code>.
     *
     * <p>
     *
     * @return the LDAP username for this <code>VIPPrincipal</code>
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Return a string representation of this <code>VIPPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this <code>VIPPrincipal</code>.
     */
    public String toString() {
        return("VIPPrincipal:  " + name);
    }
    
    /**
     * Compares the specified Object with this <code>VIPPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>VIPPrincipal</code> and the two VIPPrincipals
     * have the same username. If the object to be compared is null
     * no action is taken and no exception id thrown.
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *		<code>VIPPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *		<code>VIPPrincipal</code>.
     */
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(this == o) {
            return true;
        }
        if(!(o instanceof VIPPrincipal)) {
            return false;
        }
        VIPPrincipal that = (VIPPrincipal)o;
        
        if(this.getName().equals(that.getName())) {
            return true;
        }
        return false;
    }
    
    /**
     * Return a hash code for this <code>VIPPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>VIPPrincipal</code>.
     */
    public int hashCode() {
        return name.hashCode();
    }
}
