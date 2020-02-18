package org.slowcoders.observable;

import org.slowcoders.util.ClassUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObservableCollection<E> extends ObservableData<ObservableCollection<E>, ChangeType>
        implements Collection<E>, Serializable {

    final Collection<E> c;

    /**
     * 내부 객체(m)에 대한 외부 공유를 방지하지 위하여 객체 대신 Class 를 인자로 받는다.
     */
    public ObservableCollection(Class<? extends Collection> c) {
        this(ClassUtils.newInstance(c));
    }

    ObservableCollection(Collection<E> c) {
        this.c = c;
    }

    public int size() {
        return c.size();
    }

    public boolean isEmpty() {
        return c.isEmpty();
    }

    public boolean contains(Object o) {
        return c.contains(o);
    }

    public Object[] toArray() {
        return c.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return c.toArray(a);
    }

    public String toString() {
        return c.toString();
    }

    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<? extends E> i = c.iterator();

            public boolean hasNext() {
                return i.hasNext();
            }

            public E next() {
                return i.next();
            }

            public void remove() {
                i.remove();
                postNotification(ChangeType.Delete);
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                // Use backing collection version
                i.forEachRemaining(action);
            }
        };
    }

    public boolean add(E e) {
        if (c.add(e)) {
            postNotification(ChangeType.Create);
            return true;
        }
        return false;
    }

    public boolean remove(Object o) {
        if (c.remove(o)) {
            postNotification(ChangeType.Delete);
            return true;
        }
        return false;
    }

    public boolean containsAll(Collection<?> coll) {
        return c.containsAll(coll);
    }

    public boolean addAll(Collection<? extends E> coll) {
        if (c.addAll(coll)) {
            postNotification(ChangeType.Create);
            return true;
        }
        return false;
    }

    public boolean removeAll(Collection<?> coll) {
        if (c.removeAll(coll)) {
            postNotification(ChangeType.Delete);
            return true;
        }
        return false;
    }

    public boolean retainAll(Collection<?> coll) {
        if (c.retainAll(coll)) {
            postNotification(ChangeType.Delete);
            return true;
        }
        return false;
    }

    public void clear() {
        if (c.size() > 0) {
            c.clear();
            postNotification(ChangeType.Delete);
        }
    }

    // Override default methods in Collection
    @Override
    public void forEach(Consumer<? super E> action) {
        c.forEach(action);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        if (c.removeIf(filter)) {
            postNotification(ChangeType.Delete);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Spliterator<E> spliterator() {
        return (Spliterator<E>) c.spliterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> stream() {
        return (Stream<E>) c.stream();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> parallelStream() {
        return (Stream<E>) c.parallelStream();
    }

//    @Override
//    public ObservableData.Observer<VolatileCollection<E>, ChangeType> addAsyncObserver(ObservableData.Observer<VolatileCollection<E>, ChangeType> observer) {
//        return addAsyncObserver(observer);
//    }
//
//    @Override
//    public ObservableData.Observer<VolatileCollection<E>, ChangeType> addRealtimeObserver(ObservableData.Observer<VolatileCollection<E>, ChangeType> observer) {
//        return addRealtimeObserver(observer);
//    }
//
//    @Override
//    public ObservableData.Observer<VolatileCollection<E>, ChangeType> addWeakAsyncObserver(ObservableData.Observer<VolatileCollection<E>, ChangeType> observer) {
//        return addWeakAsyncObserver(observer);
//    }
//
//    @Override
//    public ObservableData.Observer<VolatileCollection<E>, ChangeType> addWeakRealtimeObserver(ObservableData.Observer<VolatileCollection<E>, ChangeType> observer) {
//        return addWeakRealtimeObserver(observer);
//    }
//
//    @Override
//    public boolean removeObserver(ObservableData.Observer<VolatileCollection<E>, ChangeType> observer) {
//        return removeObserver(observer);
//    }
}

