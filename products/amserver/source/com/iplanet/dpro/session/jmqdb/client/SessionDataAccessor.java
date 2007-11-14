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
 * $Id: SessionDataAccessor.java,v 1.1 2007-11-14 00:22:53 manish_rustagi Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.iplanet.dpro.session.jmqdb.client;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class SessionDataAccessor {
    // Open the indices
    public SessionDataAccessor(EntityStore store)
        throws DatabaseException {

        // Primary key for SessionRecord classes
        sessionBySID = store.getPrimaryIndex(String.class, SessionRecord.class);

        // Secondary key for SessionRecord classes
        // Last field in the getSecondaryIndex() method must be
        // the name of a class member; in this case, an SessionRecord.class
        // expDate member.
        sessionByExpDate = store.getSecondaryIndex(sessionBySID, Long.class, "expDate");

        // Secondary key for SessionRecord classes
        // Last field in the getSecondaryIndex() method must be
        // the name of a class member; in this case, an SessionRecord.class
        // uuid member.
        sessionByUUID = store.getSecondaryIndex(sessionBySID, String.class, "uuid");
    }

    // SessionRecord Accessors
    PrimaryIndex<String, SessionRecord> sessionBySID;
    SecondaryIndex<Long, String, SessionRecord> sessionByExpDate;
    SecondaryIndex<String, String, SessionRecord> sessionByUUID;
}

