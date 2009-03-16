package com.sun.identity.admin.model;

import java.util.List;

public interface Resource {
    @Override
    public String toString();
    public List<Resource> getExceptions();
}
