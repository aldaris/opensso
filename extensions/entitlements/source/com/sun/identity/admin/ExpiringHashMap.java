package com.sun.identity.admin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExpiringHashMap<K,V> extends HashMap<K, V> {

    private long duration;
    private Map<Object, Long> births = new HashMap<Object, Long>();

    public ExpiringHashMap(long duration) {
        super();
        this.duration = duration;
    }

    private void prune() {
        for (Object key : births.keySet()) {
            long birth = births.get(key).longValue();
            if (birth + duration < System.currentTimeMillis()) {
                births.remove(key);
                remove(key);
            }
        }
    }

    @Override
    public void clear() {
        births.clear();
        super.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        prune();
        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object val) {
        prune();
        return super.containsValue(val);
    }

    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        prune();
        return super.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        prune();
        return super.equals(o);
    }

    @Override
    public V get(Object key) {
        prune();
        return super.get(key);
    }

    @Override
    public int hashCode() {
        prune();
        return super.hashCode();
    }

    @Override
    public boolean isEmpty() {
        prune();
        return super.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        prune();
        return super.keySet();
    }

    @Override
    public V put(K key, V val) {
        prune();
        births.put(key, new Long(System.currentTimeMillis()));
        return super.put(key, val);
    }

    @Override
    public void putAll(Map<? extends K,? extends V> t) {
        prune();
        for (K key: t.keySet()) {
            births.put(key, new Long(System.currentTimeMillis()));
        }
        super.putAll(t);
    }

    @Override
    public V remove(Object key) {
        prune();
        births.remove(key);
        return super.remove(key);
    }

    @Override
    public int size() {
        prune();
        return super.size();
    }

    @Override
    public Collection<V> values() {
        prune();
        return super.values();
    }
}
