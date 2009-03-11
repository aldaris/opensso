package com.sun.identity.admin.model;

import java.util.List;

public interface ResourceChooserClient {
    public List<Resource> getSelectedResources();
    public List<Resource> getAvailableResources();
}
