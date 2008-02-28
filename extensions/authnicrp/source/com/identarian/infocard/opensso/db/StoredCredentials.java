/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: StoredCredentials.java,v 1.2 2008-02-28 23:31:20 superpat7 Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2008 Patrick Petit Consulting
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
