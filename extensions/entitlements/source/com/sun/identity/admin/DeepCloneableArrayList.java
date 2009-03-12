package com.sun.identity.admin;

import java.util.ArrayList;
import java.util.Collection;

public class DeepCloneableArrayList<T> extends ArrayList<T> implements DeepCloneable {
    public DeepCloneableArrayList() {
        super();
    }

    public DeepCloneableArrayList(Collection<T> o) {
        super(o);
    }

    public DeepCloneableArrayList deepClone() {
        DeepCloneableArrayList clone = new DeepCloneableArrayList();

        for (Object o: this) {
            DeepCloneable dc = (DeepCloneable)o;
            DeepCloneable dc2 = dc.deepClone();
            clone.add(dc2);
        }
        return clone;
    }
}
