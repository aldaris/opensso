package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.List;

public class UrlResourceChooserBean implements Serializable {
    private ResourceChooserClient resourceChooserClient;

    public List<Resource> getAvailableResources() {
        return resourceChooserClient.getAvailableResources();
    }

    public List<Resource> getSelectedResources() {
        return resourceChooserClient.getSelectedResources();
    }

    public ResourceChooserClient getResourceChooserClient() {
        return resourceChooserClient;
    }

    public void setResourceChooserClient(ResourceChooserClient resourceChooserClient) {
        this.resourceChooserClient = resourceChooserClient;
    }
}
