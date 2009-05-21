package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.UserViewAttribute;
import com.sun.identity.admin.model.ViewAttribute;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.SubjectAttributesManager;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.Subject;

public class UserAttributeDao implements Serializable {

    private SubjectAttributesManager getSubjectAttributesManager() {
        SSOToken t = new Token().getSSOToken();
        Subject s = SubjectUtils.createSubject(t);
        SubjectAttributesManager sam = SubjectAttributesManager.getInstance(s);

        return sam;
    }

    public List<ViewAttribute> getViewAttributes() {
        List<ViewAttribute> viewAttributes = new ArrayList<ViewAttribute>();

        SubjectAttributesManager sam = getSubjectAttributesManager();
        try {
            for (String s : sam.getAvailableSubjectAttributeNames()) {
                ViewAttribute va = new UserViewAttribute();
                va.setName(s);
                viewAttributes.add(va);
            }
        } catch (EntitlementException ee) {
            throw new AssertionError(ee);
        }
        return viewAttributes;
    }
}
