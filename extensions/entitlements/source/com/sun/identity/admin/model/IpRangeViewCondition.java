/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: IpRangeViewCondition.java,v 1.2 2009-06-04 11:49:15 veiming Exp $
 */

package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.IPCondition;
import java.io.Serializable;

public class IpRangeViewCondition
    extends ViewCondition
    implements Serializable {

    private int[] startIp = new int[4];
    private int[] endIp = new int[4];

    public EntitlementCondition getEntitlementCondition() {
        IPCondition ipc = new IPCondition();

        ipc.setStartIp(getIpString(startIp));
        ipc.setEndIp(getIpString(endIp));

        return ipc;
    }

    private String getIpString(int[] parts) {
        String s = parts[0]+"."+parts[1]+"."+parts[2]+"."+parts[3];
        return s;
    }

    public int[] getEndIp() {
        return endIp;
    }

    public void setEndIp(int[] endIp) {
        this.endIp = endIp;
    }

    public int[] getStartIp() {
        return startIp;
    }

    public void setStartIp(int[] startIp) {
        this.startIp = startIp;
    }

    @Override
    public String toString() {
        return getTitle() + ":{" + getIpString(startIp) + ">" + getIpString(endIp) + "}";
    }
}
