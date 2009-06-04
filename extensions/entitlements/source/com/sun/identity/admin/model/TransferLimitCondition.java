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
 * $Id: TransferLimitCondition.java,v 1.3 2009-06-04 11:49:18 veiming Exp $
 */

package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import com.sun.identity.entitlement.BankTransferLimitCondition;
import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class TransferLimitCondition
    extends ViewCondition
    implements Serializable {

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public LimitType getLimitType() {
        return limitType;
    }

    public void setLimitType(LimitType limitType) {
        this.limitType = limitType;
    }

    public enum LimitType {
        LOWER,
        UPPER;

        public List<SelectItem> getItems() {
            List<SelectItem> items = new ArrayList<SelectItem>();
            for (LimitType lt: values()) {
               items.add(new SelectItem(lt, lt.getTitle()));
            }

            return items;
        }

        public String getTitle() {
            Resources r = new Resources();
            return r.getString(this, toString() + ".title");
        }


    }
    
    private int limit = 0;
    private LimitType limitType = LimitType.UPPER;

    public EntitlementCondition getEntitlementCondition() {
        BankTransferLimitCondition btlc = new BankTransferLimitCondition();
        btlc.setTransferLimit(limit);
        switch (limitType) {
            case LOWER:
                btlc.setLimitType(btlc.MIN_TRANSFER_LIMIT);
                break;
            case UPPER:
                btlc.setLimitType(btlc.MAX_TRANSFER_LIMIT);
                break;
        }

        return btlc;
    }

    public String getLimitTypeString() {
        return limitType.toString();
    }

    public void setLimitTypeString(String limitTypeString) {
        limitType = LimitType.valueOf(limitTypeString);
    }

    @Override
    public String toString() {
        return getTitle() + ": {" + limitType.getTitle() + " " + limit + "}";
    }
}
