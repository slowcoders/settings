package org.slowcoders.observable;

import org.slowcoders.util.ClassUtils;

import java.util.List;
import java.util.RandomAccess;

public class ObservableRandomAccessList<E> extends ObservableList<E> implements RandomAccess {

    public ObservableRandomAccessList(Class<? extends List> list) {
        this(ClassUtils.newInstance(list));
    }

    ObservableRandomAccessList(List<E> list) {
        super(list);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return new ObservableRandomAccessList<>(
                list.subList(fromIndex, toIndex));
    }

    private static final long serialVersionUID = -2542308836966382001L;

    private Object writeReplace() {
        return new ObservableList<>(list);
    }
}
