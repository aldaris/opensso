package com.sun.identity.admin.model;

import java.io.Serializable;

public abstract class ResourceDecorator implements Serializable {
    public abstract void decorate(Resource r);
}
