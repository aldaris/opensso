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
 * $Id: SessionRecord.java,v 1.1 2007-11-14 00:22:53 manish_rustagi Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
 
package com.iplanet.dpro.session.jmqdb.client;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class SessionRecord {

    // Data fields:
    //     - expdate           => 8 bytes
    // Secondary key is the expDate
    @SecondaryKey(relate=Relationship.MANY_TO_ONE)
    private Long expDate;
    
    //     - uuid              => variable length
    // Secondary key is the UUID
    @SecondaryKey(relate=Relationship.MANY_TO_ONE)
    private String uuid = null;
    
    //     - masterSID         => variable length
    // Primary key is the masterSID
    // This assumes that the masterSID is
    // unique in the database.
    @PrimaryKey
    private String sid = null;
    
    private String masterSID = null;
    
    //     - session state state      => 4 bytes
    private int sessionState;

    //     - session BLOB      => variable length
    private byte[] sessionBlob = null;
    
    public void setSID(String sid) {
        this.sid = sid;
    }

    public void setExpDate(long date) {
        expDate = new Long(date);
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setMasterSID(String sid) {
        masterSID = sid;
    }

    public void setSessionState(int ss) {
        sessionState = ss;
    }

    public void setSessionBlob(byte[] blob) {
        sessionBlob = blob;
    }

    public String getSID() {
        return sid;
    }
    
    public Long getExpDate() {
        return expDate;
    }

    public String getUUID() {
        return uuid;
    }

    public String getMasterSID() {
        return masterSID;
    }

    public int getSessionState() {
        return sessionState;
    }

    public byte[] getSessionBlob() {
        return sessionBlob;
    }
}

