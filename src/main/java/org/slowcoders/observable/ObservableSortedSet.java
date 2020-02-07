package org.slowcoders.observable;

import org.slowcoders.util.ClassUtils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedSet;

public class ObservableSortedSet<E> extends ObservableSet<E> implements SortedSet<E>, Serializable {

    private final SortedSet<E> ss;

    public ObservableSortedSet(Class<? extends SortedSet> s) {
        this(ClassUtils.newInstance(s));
    }

    protected ObservableSortedSet(SortedSet<E> s) {
        super(s);
        ss = s;
    }

    public Comparator<? super E> comparator() {
        return ss.comparator();
    }

    public SortedSet<E> subSet(E fromElement, E toElement) {
        return new ObservableSortedSet<>(ss.subSet(fromElement, toElement));
    }

    public SortedSet<E> headSet(E toElement) {
        return new ObservableSortedSet<>(ss.headSet(toElement));
    }

    public SortedSet<E> tailSet(E fromElement) {
        return new ObservableSortedSet<>(ss.tailSet(fromElement));
    }

    public E first() {
        return ss.first();
    }

    public E last() {
        return ss.last();
    }

}
