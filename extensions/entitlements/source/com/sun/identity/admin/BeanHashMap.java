package com.sun.identity.admin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class BeanHashMap<K,V> extends HashMap<K,V> {
    public Collection<V> getValues() {
        return values();
    }

    public Set<K> getKeys() {
        return keySet();
    }
}
