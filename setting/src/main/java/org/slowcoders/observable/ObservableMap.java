package org.slowcoders.observable;

import org.slowcoders.util.Debug;
import org.slowcoders.util.ClassUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ObservableMap<K, V> extends ObservableData<ObservableMap<K, V>, ChangeType> implements Map<K, V>, Serializable {

    private final Map<K, V> m;

    /**
     * 내부 객체(m)에 대한 외부 공유를 방지하지 위하여 객체 대신 Class 를 인자로 받는다.
     */
    public ObservableMap(Class<? extends Map<K, V>> m) {
        this(ClassUtils.newInstance(m));
    }

    ObservableMap(Map<K, V> m) {
        if (m == null)
            throw new NullPointerException();
        this.m = m;
    }

    public int size() {
        return m.size();
    }

    public boolean isEmpty() {
        return m.isEmpty();
    }

    public boolean containsKey(Object key) {
        return m.containsKey(key);
    }

    public boolean containsValue(Object val) {
        return m.containsValue(val);
    }

    public V get(Object key) {
        return m.get(key);
    }

    public V put(K key, V value) {
        V item = m.put(key, value);
        if (item != null) {
            postNotification(ChangeType.Create);
        }
        return item;
    }

    public V remove(Object key) {
        V item = m.remove(key);
        if (item != null) {
            postNotification(ChangeType.Delete);
        }
        return item;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        if (m.size() > 0) {
            this.m.putAll(m);
            postNotification(ChangeType.Create);
        }
    }

    public void clear() {
        if (m.size() > 0) {
            this.m.clear();
            postNotification(ChangeType.Delete);
        }
    }

    private transient Set<K> keySet;
    private transient Set<Entry<K, V>> entrySet;
    private transient Collection<V> values;

    public Set<K> keySet() {
        if (keySet == null)
            keySet = new ObservableSet<K>(m.keySet());
        return keySet;
    }

    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null)
            entrySet = new ObservableEntrySet<>(m.entrySet());
        return entrySet;
    }

    public Collection<V> values() {
        if (values == null)
            values = new ObservableCollection<V>(m.values());
        return values;
    }

    public boolean equals(Object o) {
        return o == this || m.equals(o);
    }

    public int hashCode() {
        return m.hashCode();
    }

    public String toString() {
        return m.toString();
    }

    // Override default methods in Map
    @Override
    @SuppressWarnings("unchecked")
    public V getOrDefault(Object k, V defaultValue) {
        // Safe cast as we don't change the value
        return ((Map<K, V>) m).getOrDefault(k, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        m.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.m.replaceAll(function);
        postNotification(ChangeType.Update);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V item = this.m.putIfAbsent(key, value);
        if (item != null) {
            postNotification(ChangeType.Create);
        }
        return item;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (this.m.remove(key, value)) {
            postNotification(ChangeType.Delete);
            return true;
        }
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (this.m.replace(key, oldValue, newValue)) {
            postNotification(ChangeType.Update);
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        V item = this.m.replace(key, value);
        if (item != null) {
            postNotification(ChangeType.Update);
        }
        return item;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V item = this.m.computeIfAbsent(key, mappingFunction);
        if (item != null) {
            postNotification(ChangeType.Update);
        }
        return item;
    }

    @Override
    public V computeIfPresent(K key,
                              BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V item = this.m.computeIfPresent(key, remappingFunction);
        if (item != null) {
            postNotification(ChangeType.Update);
        }
        return item;
    }

    @Override
    public V compute(K key,
                     BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V item = this.m.compute(key, remappingFunction);
        if (item != null) {
            postNotification(ChangeType.Update);
        }
        return item;
    }

    @Override
    public V merge(K key, V value,
                   BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        V item = this.m.merge(key, value, remappingFunction);
        if (item != null) {
            postNotification(ChangeType.Update);
        }
        return item;
    }

    /**
     * We need this class in addition to ObservableSet as
     * Map.Entries themselves permit modification of the backing Map
     * via their setValue operation.  This class is subtle: there are
     * many possible attacks that must be thwarted.
     *
     * @serial include
     */
    public static class ObservableEntrySet<K, V>
            extends ObservableSet<Entry<K, V>> {
        private static final long serialVersionUID = 7854390611657943733L;


        private ObservableEntrySet(Set<? extends Entry<? extends K, ? extends V>> s) {
            // Need to cast to raw in order to work around a limitation in the type system
            super((Set) s);
        }

        static <K, V> Consumer<Entry<K, V>> entryConsumer(Consumer<? super Entry<K, V>> action) {
            return e -> action.accept(new ObservableEntry<>(e));
        }

        public void forEach(Consumer<? super Entry<K, V>> action) {
            Objects.requireNonNull(action);
            c.forEach(entryConsumer(action));
        }

        static final class ObservableEntrySetSpliterator<K, V>
                implements Spliterator<Entry<K, V>> {
            final Spliterator<Entry<K, V>> s;

            ObservableEntrySetSpliterator(Spliterator<Entry<K, V>> s) {
                this.s = s;
            }

            @Override
            public boolean tryAdvance(Consumer<? super Entry<K, V>> action) {
                Objects.requireNonNull(action);
                return s.tryAdvance(entryConsumer(action));
            }

            @Override
            public void forEachRemaining(Consumer<? super Entry<K, V>> action) {
                Objects.requireNonNull(action);
                s.forEachRemaining(entryConsumer(action));
            }

            @Override
            public Spliterator<Entry<K, V>> trySplit() {
                Spliterator<Entry<K, V>> split = s.trySplit();
                return split == null
                        ? null
                        : new ObservableEntrySetSpliterator<>(split);
            }

            @Override
            public long estimateSize() {
                return s.estimateSize();
            }

            @Override
            public long getExactSizeIfKnown() {
                return s.getExactSizeIfKnown();
            }

            @Override
            public int characteristics() {
                return s.characteristics();
            }

            @Override
            public boolean hasCharacteristics(int characteristics) {
                return s.hasCharacteristics(characteristics);
            }

            @Override
            public Comparator<? super Entry<K, V>> getComparator() {
                return s.getComparator();
            }
        }

        @SuppressWarnings("unchecked")
        public Spliterator<Entry<K, V>> spliterator() {
            return new ObservableEntrySetSpliterator<>(
                    (Spliterator<Entry<K, V>>) c.spliterator());
        }

        @Override
        public Stream<Entry<K, V>> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        @Override
        public Stream<Entry<K, V>> parallelStream() {
            return StreamSupport.stream(spliterator(), true);
        }

        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                private final Iterator<? extends Entry<K, V>> i = c.iterator();

                public boolean hasNext() {
                    return i.hasNext();
                }

                public Entry<K, V> next() {
                    return new ObservableEntry<>(i.next());
                }

                public void remove() {
                    i.remove();
                    postNotification(ChangeType.Delete);
                }
            };
        }

        @SuppressWarnings("unchecked")
        public Object[] toArray() {
            Object[] a = c.toArray();
            for (int i = 0; i < a.length; i++)
                a[i] = new ObservableEntry<>((Entry<? extends K, ? extends V>) a[i]);
            return a;
        }

        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            // We don't pass a to c.toArray, to avoid window of
            // vulnerability wherein an unscrupulous multithreaded client
            // could get his hands on raw (unwrapped) Entries from c.
            Object[] arr = c.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));

            for (int i = 0; i < arr.length; i++)
                arr[i] = new ObservableEntry<>((Entry<? extends K, ? extends V>) arr[i]);

            if (arr.length > a.length)
                return (T[]) arr;

            System.arraycopy(arr, 0, a, 0, arr.length);
            if (a.length > arr.length)
                a[arr.length] = null;
            return a;
        }

        /**
         * This method is overridden to protect the backing set against
         * an object with a nefarious equals function that senses
         * that the equality-candidate is Map.Entry and calls its
         * setValue method.
         */
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            return c.contains(
                    new ObservableEntry<>((Entry<?, ?>) o));
        }

        /**
         * The next two methods are overridden to protect against
         * an unscrupulous List whose contains(Object o) method senses
         * when o is a Map.Entry, and calls o.setValue.
         */
        public boolean containsAll(Collection<?> coll) {
            for (Object e : coll) {
                if (!contains(e)) // Invokes safe contains() above
                    return false;
            }
            return true;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;

            if (!(o instanceof Set))
                return false;
            Set<?> s = (Set<?>) o;
            if (s.size() != c.size())
                return false;
            return containsAll(s); // Invokes safe containsAll() above
        }

        /**
         * This "wrapper class" serves two purposes: it prevents
         * the client from modifying the backing Map, by short-circuiting
         * the setValue method, and it protects the backing Map against
         * an ill-behaved Map.Entry that attempts to modify another
         * Map Entry when asked to perform an equality check.
         */
        static class ObservableEntry<K, V> implements Entry<K, V> {

            private final ObservableData<ObservableMap<K, V>, ChangeType> delegate = new ObservableData<>();
            private Entry<K, V> e;

            ObservableEntry(Class<Entry<K, V>> e) {
                try {
                    this.e = e.newInstance();
                } catch (Exception e1) {
                    throw Debug.wtf(e1);
                }
            }

            ObservableEntry(Entry<K, V> e) {
                this.e = e;
            }

            public K getKey() {
                return e.getKey();
            }

            public V getValue() {
                return e.getValue();
            }

            public V setValue(V value) {
                V item = e.setValue(value);
                if (item != null) {
                    delegate.postNotification(ChangeType.Update);
                }
                return item;
            }

            public int hashCode() {
                return e.hashCode();
            }

            public boolean equals(Object o) {
                if (this == o)
                    return true;
                if (!(o instanceof Map.Entry))
                    return false;
                Entry<?, ?> t = (Entry<?, ?>) o;
                return eq(e.getKey(), t.getKey()) &&
                        eq(e.getValue(), t.getValue());
            }

            public String toString() {
                return e.toString();
            }

            private boolean eq(Object o1, Object o2) {
                return o1==null ? o2==null : o1.equals(o2);
            }

        }
    }

}
