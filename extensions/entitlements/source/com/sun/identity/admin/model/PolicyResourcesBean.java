package com.sun.identity.admin.model;

import java.util.List;
import javax.faces.model.SelectItem;

public interface PolicyResourcesBean {
    public List<SelectItem> getAvailableResourceItems();
    public List<Resource> getAvailableResources();
    public PrivilegeBean getPrivilegeBean();
}
