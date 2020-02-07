package org.slowcoders.observable;

import org.slowcoders.util.ClassUtils;

import java.io.Serializable;
import java.util.NavigableMap;
import java.util.NavigableSet;

/**
 * @serial include
 */
public class ObservableNavigableMap<K, V> extends ObservableSortedMap<K, V> implements NavigableMap<K, V>, Serializable {

    private static final long serialVersionUID = -4858195264774772197L;

    /**
     * The instance we wrap and protect.
     */
    private final NavigableMap<K, V> nm;

    public ObservableNavigableMap(Class<NavigableMap<K, V>> m) {
        this(ClassUtils.newInstance(m));
    }

    private ObservableNavigableMap(NavigableMap<K, V> m) {
        super(m);
        nm = m;
    }

    public K lowerKey(K key) {
        return nm.lowerKey(key);
    }

    public K floorKey(K key) {
        return nm.floorKey(key);
    }

    public K ceilingKey(K key) {
        return nm.ceilingKey(key);
    }

    public K higherKey(K key) {
        return nm.higherKey(key);
    }

    @SuppressWarnings("unchecked")
    public Entry<K, V> lowerEntry(K key) {
        Entry<K, V> lower = (Entry<K, V>) nm.lowerEntry(key);
        return (null != lower) ? new ObservableEntrySet.ObservableEntry<>(lower) : null;
    }

    @SuppressWarnings("unchecked")
    public Entry<K, V> floorEntry(K key) {
        Entry<K, V> floor = (Entry<K, V>) nm.floorEntry(key);
        return (null != floor) ? new ObservableEntrySet.ObservableEntry<>(floor) : null;
    }

    @SuppressWarnings("unchecked")
    public Entry<K, V> ceilingEntry(K key) {
        Entry<K, V> ceiling = (Entry<K, V>) nm.ceilingEntry(key);
        return (null != ceiling) ? new ObservableEntrySet.ObservableEntry<>(ceiling) : null;
    }


    @SuppressWarnings("unchecked")
    public Entry<K, V> higherEntry(K key) {
        Entry<K, V> higher = (Entry<K, V>) nm.higherEntry(key);
        return (null != higher) ? new ObservableEntrySet.ObservableEntry<>(higher) : null;
    }

    @SuppressWarnings("unchecked")
    public Entry<K, V> firstEntry() {
        Entry<K, V> first = (Entry<K, V>) nm.firstEntry();
        return (null != first) ? new ObservableEntrySet.ObservableEntry<>(first) : null;
    }

    @SuppressWarnings("unchecked")
    public Entry<K, V> lastEntry() {
        Entry<K, V> last = (Entry<K, V>) nm.lastEntry();
        return (null != last) ? new ObservableEntrySet.ObservableEntry<>(last) : null;
    }

    public Entry<K, V> pollFirstEntry() {
        Entry<K, V> entry = nm.pollFirstEntry();
        if (entry != null) {
            postNotification(ChangeType.Delete);
        }
        return entry;
    }

    public Entry<K, V> pollLastEntry() {
        Entry<K, V> entry = nm.pollLastEntry();
        if (entry != null) {
            postNotification(ChangeType.Delete);
        }
        return entry;
    }

    public NavigableMap<K, V> descendingMap() {
        return new ObservableNavigableMap<>(nm.descendingMap());
    }

    public NavigableSet<K> navigableKeySet() {
        return new ObservableNavigableSet<>(nm.navigableKeySet());
    }

    public NavigableSet<K> descendingKeySet() {
        return new ObservableNavigableSet<>(nm.descendingKeySet());
    }

    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return new ObservableNavigableMap<>(
                nm.subMap(fromKey, fromInclusive, toKey, toInclusive));
    }

    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return new ObservableNavigableMap<>(nm.headMap(toKey, inclusive));
    }

    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return new ObservableNavigableMap<>(nm.tailMap(fromKey, inclusive));
    }
}
