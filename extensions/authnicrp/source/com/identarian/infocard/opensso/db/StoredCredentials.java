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

package com.identarian.infocard.opensso.db;

import java.io.Serializable;
//import javax.persistence.Id;

/**
 *
 * @author Patrick Petit at PatrickPetit.COM
 */
//@Entity
//@Table(name="StoredCredentials")
public class StoredCredentials implements Serializable {
    
    private static final long serialVersionUID = 1L;
    //@Id
    private String ppid;
    private String userID;
    private String userPasswd;
    
    protected StoredCredentials() {
    }
    
    public StoredCredentials(String ppid, String uid, String passwd) {
        this.ppid = ppid;
        this.userID = uid;
        this.userPasswd = passwd;
    }
    
    public void setId(String id) {
        this.setPpid(id);
    }
    
    public String getId() {
        return getPpid();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getPpid() != null ? getPpid().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StoredCredentials)) {
            return false;
        }
        StoredCredentials other = (StoredCredentials) object;
        if ((this.getPpid() == null && other.getPpid() != null) ||
            (this.getPpid() != null && !this.ppid.equals(other.ppid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StoredCredetial[id=" + getPpid() + "]";
    }

    public String getPpid() {
        return ppid;
    }

    public void setPpid(String ppid) {
        this.ppid = ppid;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPasswd() {
        return userPasswd;
    }

    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd;
    }
}
