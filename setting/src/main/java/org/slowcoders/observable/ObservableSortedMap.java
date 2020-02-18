package org.slowcoders.observable;

import org.slowcoders.util.ClassUtils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedMap;

/**
 * @serial include
 */
public class ObservableSortedMap<K, V> extends ObservableMap<K, V> implements SortedMap<K, V>, Serializable {

    private static final long serialVersionUID = -8806743815996713206L;

    private final SortedMap<K, V> sm;

    public ObservableSortedMap(Class<? extends SortedMap<K, V>> m) {
        this(ClassUtils.newInstance(m));
    }

    ObservableSortedMap(SortedMap<K, V> m) {
        super(m);
        sm = m;
    }

    public Comparator<? super K> comparator() {
        return sm.comparator();
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return new ObservableSortedMap<>(sm.subMap(fromKey, toKey));
    }

    public SortedMap<K, V> headMap(K toKey) {
        return new ObservableSortedMap<>(sm.headMap(toKey));
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        return new ObservableSortedMap<>(sm.tailMap(fromKey));
    }

    public K firstKey() {
        return sm.firstKey();
    }

    public K lastKey() {
        return sm.lastKey();
    }
}
