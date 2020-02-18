package org.slowcoders.observable;

import org.slowcoders.util.ClassUtils;

import java.io.Serializable;
import java.util.Set;

public class ObservableSet<E> extends ObservableCollection<E> implements Set<E>, Serializable {

    public ObservableSet(Class<? extends Set> s) {
        this(ClassUtils.newInstance(s));
    }

    protected ObservableSet(Set<E> s) {
        super(s);
    }

    public boolean equals(Object o) {
        return o == this || c.equals(o);
    }

    public int hashCode() {
        return c.hashCode();
    }
}

