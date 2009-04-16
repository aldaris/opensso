package com.sun.identity.admin;

import java.util.ArrayList;
import java.util.Collection;

public class CircularArrayList<E> extends ArrayList<E> {

    public CircularArrayList(Collection<? extends E> c) {
        super(c);
    }

    public CircularArrayList() {
        super();
    }

    @Override
    public E get(int i) {
        return super.get(i % size());
    }
}
