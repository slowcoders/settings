package org.slowcoders.observable;

import org.slowcoders.util.ClassUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NavigableSet;

public class ObservableNavigableSet<E> extends ObservableSortedSet<E> implements NavigableSet<E>, Serializable {

    private static final long serialVersionUID = -6027448201786391929L;
    /**
     * The instance we are protecting.
     */
    private final NavigableSet<E> ns;

    public ObservableNavigableSet(Class<NavigableSet<E>> s) {
        this(ClassUtils.newInstance(s));
    }

    ObservableNavigableSet(NavigableSet<E> s) {
        super(s);
        ns = s;
    }


    public E lower(E e) {
        return ns.lower(e);
    }

    public E floor(E e) {
        return ns.floor(e);
    }

    public E ceiling(E e) {
        return ns.ceiling(e);
    }

    public E higher(E e) {
        return ns.higher(e);
    }

    public E pollFirst() {
        E item = ns.pollFirst();
        if (item != null) {
            postNotification(ChangeType.Delete);
        }
        return item;
    }

    public E pollLast() {
        E item = ns.pollLast();
        if (item != null) {
            postNotification(ChangeType.Delete);
        }
        return item;
    }

    public NavigableSet<E> descendingSet() {
        return new ObservableNavigableSet<>(ns.descendingSet());
    }

    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new ObservableNavigableSet<>(
                ns.subSet(fromElement, fromInclusive, toElement, toInclusive));
    }

    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new ObservableNavigableSet<>(
                ns.headSet(toElement, inclusive));
    }

    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new ObservableNavigableSet<>(
                ns.tailSet(fromElement, inclusive));
    }

}
