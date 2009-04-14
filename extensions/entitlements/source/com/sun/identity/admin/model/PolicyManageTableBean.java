package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolicyManageTableBean implements Serializable {

    public static class SortKey implements Serializable {
        private boolean ascending = false;
        private String column = "name";

        public SortKey() {
            // nothing
        }

        public SortKey(String column, boolean ascending) {
            this.column = column;
            this.ascending = ascending;
        }

        public boolean isAscending() {
            return ascending;
        }

        public void setAscending(boolean ascending) {
            this.ascending = ascending;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SortKey)) {
                return false;
            }

            SortKey other = (SortKey)o;
            return other.toString().endsWith(toString());
        }

        @Override
        public String toString() {
            return column+":"+ascending;
        }
    }

    private SortKey sortKey = new SortKey();
    private List<PrivilegeBean> privilegeBeans;
    private static Map<SortKey,Comparator> comparators = new HashMap<SortKey,Comparator>();

    static {
        comparators.put(new SortKey("name", true), new PrivilegeBean.NameComparator(true));
        comparators.put(new SortKey("name", false), new PrivilegeBean.NameComparator(false));
    }

    public PolicyManageTableBean(List<PrivilegeBean> privilegeBeans) {
        this.privilegeBeans = privilegeBeans;
    }

    public SortKey getSortKey() {
        return sortKey;
    }

    public void setSortKey(SortKey sortKey) {
        this.sortKey = sortKey;
    }

    public void sort() {
        Comparator c = comparators.get(sortKey);
        Collections.sort(privilegeBeans, c);
    }
}
