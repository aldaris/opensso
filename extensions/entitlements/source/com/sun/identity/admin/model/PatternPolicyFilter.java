package com.sun.identity.admin.model;

import com.sun.identity.entitlement.util.PrivilegeSearchFilter;
import java.util.Collections;
import java.util.List;

public abstract class PatternPolicyFilter extends PolicyFilter {
    private String filter;

    public abstract String getPrivilegeAttributeName();

    private String getPattern(String filter) {
        String pattern;
        if (filter == null || filter.length() == 0) {
            pattern = "*";
        } else {
            pattern = "*" + filter + "*";
        }

        return pattern;
    }

    public List<PrivilegeSearchFilter> getPrivilegeSearchFilters() {
        String pattern = getPattern(getFilter());
        PrivilegeSearchFilter psf = new PrivilegeSearchFilter(getPrivilegeAttributeName(), pattern);
        return Collections.singletonList(psf);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
