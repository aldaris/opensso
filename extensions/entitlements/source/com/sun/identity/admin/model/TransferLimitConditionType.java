package com.sun.identity.admin.model;

import com.sun.identity.entitlement.BankTransferLimitCondition;
import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;

public class TransferLimitConditionType
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new TransferLimitCondition();
        vc.setConditionType(this);

        return vc;
    }

    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        assert(ec instanceof BankTransferLimitCondition);
        BankTransferLimitCondition btlc = (BankTransferLimitCondition)ec;

        TransferLimitCondition tlc = (TransferLimitCondition)newViewCondition();
        tlc.setLimit(btlc.getTransferLimit());
        if (btlc.getLimitType().equals(btlc.MIN_TRANSFER_LIMIT)) {
            tlc.setLimitType(TransferLimitCondition.LimitType.LOWER);
        } else if (btlc.getLimitType().equals(btlc.MAX_TRANSFER_LIMIT)) {
            tlc.setLimitType(TransferLimitCondition.LimitType.UPPER);
        } else {
            throw new AssertionError("unknown limit type in entitlement condition: " + btlc.getLimitType());
        }
        return tlc;

    }
}
