package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.UserAttributeDao;
import com.sun.identity.admin.handler.UserAttributesHandler;
import com.sun.identity.entitlement.ResourceAttribute;
import com.sun.identity.entitlement.UserAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.comparators.NullComparator;
import static com.sun.identity.admin.model.AttributesBean.AttributeType.*;

public class UserAttributesBean extends AttributesBean {

    private String filter = "";
    private List<ViewAttribute> availableViewAttributes = new ArrayList<ViewAttribute>();

    public UserAttributesBean() {
        super();
        loadAvailableViewAttributes();
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        if (filter == null) {
            filter = "";
        }
        NullComparator n = new NullComparator();
        if (n.compare(this.filter, filter) != 0) {
            this.filter = filter;
            loadAvailableViewAttributes();
        }
    }

    public List<ViewAttribute> getAvailableViewAttributes() {
        return availableViewAttributes;
    }

    public void loadAvailableViewAttributes() {
        UserAttributeDao uadao = new UserAttributeDao();
        availableViewAttributes.clear();

        for (ViewAttribute va : uadao.getViewAttributes()) {
            if (filter == null || filter.length() == 0) {
                availableViewAttributes.add(va);
            } else {
                if (va.getName().toLowerCase().contains(filter.toLowerCase())) {
                    availableViewAttributes.add(va);
                } else if (va.getTitle().toLowerCase().contains(filter.toLowerCase())) {
                    availableViewAttributes.add(va);
                }
            }
        }
    }

    public AttributeType getAttributeType() {
        return USER;
    }

    public ViewAttribute newViewAttribute() {
        return new UserViewAttribute();
    }

    public UserAttributesBean(Set<ResourceAttribute> resourceAttributes) {
        this();

        for (ResourceAttribute ras : resourceAttributes) {
            if (!(ras instanceof UserAttributes)) {
                continue;
            }

            String key = ras.getPropertyName();
            UserViewAttribute uva = new UserViewAttribute();
            uva.setName(key);
            getViewAttributes().add(uva);
        }
    }

    public Set<ResourceAttribute> toResourceAttributesSet() {
        Set<ResourceAttribute> resAttributes = new HashSet<ResourceAttribute>();

        for (ViewAttribute va : getViewAttributes()) {
            if (!(va instanceof UserViewAttribute)) {
                continue;
            }
            UserViewAttribute uva = (UserViewAttribute) va;
            UserAttributes uas = new UserAttributes();

            uas.setPropertyName(uva.getName());
            resAttributes.add(uas);
        }

        return resAttributes;
    }

    @Override
    public void reset() {
        super.reset();
        setAttributesHandler(new UserAttributesHandler(this));
    }
}
