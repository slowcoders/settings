package org.slowcoders.observable;

import org.slowcoders.util.ClassUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ObservableList<E> extends ObservableCollection<E> implements List<E> {

    final List<E> list;

    public ObservableList(Class<? extends List> list) {
        this(ClassUtils.newInstance(list));
    }

    ObservableList(List<E> list) {
        super(list);
        this.list = list;
    }

    public boolean equals(Object o) {
        return o == this || list.equals(o);
    }

    public int hashCode() {
        return list.hashCode();
    }

    public E get(int index) {
        return list.get(index);
    }

    public E set(int index, E element) {
        E item = list.set(index, element);
        postNotification(ChangeType.Update);
        return item;
    }

    public void add(int index, E element) {
        list.add(index, element);
        postNotification(ChangeType.Create);
    }

    public E remove(int index) {
        E item = list.remove(index);
        if (item != null) {
            postNotification(ChangeType.Delete);
        }
        return item;
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        if (list.addAll(c)) {
            postNotification(ChangeType.Create);
            return true;
        }
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        list.replaceAll(operator);
        postNotification(ChangeType.Update);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        list.sort(c);
        postNotification(ChangeType.Move);
    }

    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    public ListIterator<E> listIterator(final int index) {
        return new ListIterator<E>() {
            private final ListIterator<E> i
                    = list.listIterator(index);

            public boolean hasNext() {
                return i.hasNext();
            }

            public E next() {
                return i.next();
            }

            public boolean hasPrevious() {
                return i.hasPrevious();
            }

            public E previous() {
                return i.previous();
            }

            public int nextIndex() {
                return i.nextIndex();
            }

            public int previousIndex() {
                return i.previousIndex();
            }

            public void remove() {
                i.remove();
                postNotification(ChangeType.Delete);
            }

            public void set(E e) {
                i.set(e);
                postNotification(ChangeType.Update);
            }

            public void add(E e) {
                i.add(e);
                postNotification(ChangeType.Create);
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                i.forEachRemaining(action);
            }
        };
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return new ObservableList<>(list.subList(fromIndex, toIndex));
    }

    private Object readResolve() {
        return (list instanceof RandomAccess ? new ObservableRandomAccessList<>(list) : this);
    }

}
