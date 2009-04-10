package com.sun.identity.admin;

import java.util.Collection;
import java.util.List;

public class Functions {
    public static String indexOf(List l, Object o) {
        return Integer.toString(l.indexOf(o));
    }

    public static String truncate(String s, int length) {
        if (length >= s.length()) {
            return s;
        }
        return s.substring(0, length-1)+"...";
    }

    public static int size(Collection c) {
        return c.size();
    }
}
