package com.sun.identity.admin.model;

import com.sun.identity.admin.handler.StaticAttributesHandler;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.ResourceAttributes;
import com.sun.identity.entitlement.StaticAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StaticAttributesBean extends AttributesBean {
    private String addPopupValue;

    public StaticAttributesBean() {
        super();
    }

    public StaticAttributesBean(Set<ResourceAttributes> resourceAttributes) {
        this();

        for (ResourceAttributes ras: resourceAttributes) {
            if (!(ras instanceof StaticAttributes)) {
                continue;
            }

            for (String key: ras.getProperties().keySet()) {
                for (String val: ras.getProperties().get(key)) {
                    StaticViewAttribute sva = new StaticViewAttribute();
                    sva.setName(key);
                    sva.setValue(val);
                    getViewAttributes().add(sva);
                }
            }
        }
    }

    public Set<ResourceAttributes> toResourceAttributesSet() {
        Map<String,Set<String>> properties = new HashMap<String,Set<String>>();

        for (ViewAttribute va: getViewAttributes()) {
            if (!(va instanceof StaticViewAttribute)) {
                continue;
            }
            StaticViewAttribute sva = (StaticViewAttribute)va;
            properties.put(sva.getName(), Collections.singleton(sva.getValue()));
        }

        ResourceAttributes sas = new StaticAttributes();
        try {
            sas.setProperties(properties);
        } catch (EntitlementException ee) {
            throw new AssertionError(ee);
        }

        return Collections.singleton(sas);
    }

    @Override
    public void reset() {
        super.reset();
        setAttributesHandler(new StaticAttributesHandler(this));
        addPopupValue = null;
    }

    public String getAddPopupValue() {
        return addPopupValue;
    }

    public void setAddPopupValue(String addPopupValue) {
        this.addPopupValue = addPopupValue;
    }
}
