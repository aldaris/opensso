/*
 * Copyright (c) 2008, Patrick Petit Consulting, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names Patrick Petit, Patrick Petit Consulting,
 *       PatrickPetit.com, identarian.com nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.identarian.infocard.opensso.rp;

import java.io.Serializable;
import java.security.Principal;

public class InfocardPrincipal implements Principal, Serializable {
    private String name;

    public InfocardPrincipal(String name) {
        if (name == null) {
            throw new NullPointerException("illegal null input");
        }
        this.name = name;
    }

    /**
     * Returns the LDAP username for this <code>InfocardPrincipal</code>.
     *
     * @return the LDAP username for this <code>InfocardPrincipal</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of this <code>InfocardPrincipal</code>.
     *
     * @return a string representation of this <code>InfocardPrincipal</code>.
     */
    @Override
    public String toString() {
        return(this.getClass().getName() + ":" + name);
    }

    /**
     * Compares the specified Object with this <code>InfocardPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>InfocardPrincipal</code> and the two SamplePrincipals
     * have the same username.
     *
     * @param o Object to be compared for equality with this
     *        <code>InfocardPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *         <code>InfocardPrincipal</code>.
     */
    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof InfocardPrincipal)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        InfocardPrincipal that = (InfocardPrincipal)o;
        return this.getName().equals(that.getName());
    }
 
    /**
     * Return a hash code for this <code>InfocardPrincipal</code>.
     *
     * @return a hash code for this <code>InfocardPrincipal</code>.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
