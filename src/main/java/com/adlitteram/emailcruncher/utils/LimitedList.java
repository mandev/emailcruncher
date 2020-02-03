package com.adlitteram.emailcruncher.utils;

import java.util.Collection;
import java.util.LinkedList;

public class LimitedList<E> extends LinkedList<E> {

    private final int limit;

    public LimitedList(Collection<? extends E> c) {
        super(c);
        limit = 16;
    }

    public LimitedList(int limit) {
        this.limit = limit;
    }

    @Override
    public void addFirst(E o) {
        super.remove(o);
        super.addFirst(o);
        while (size() > limit) {
            super.removeLast();
        }
    }
}
