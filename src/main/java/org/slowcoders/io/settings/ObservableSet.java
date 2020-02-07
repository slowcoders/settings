package org.slowcoders.io.settings;

import com.google.common.collect.ImmutableSet;
import org.slowcoders.io.serialize.IOAdapter;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObservableSet<E> extends ObservableOption<Set<E>> implements Iterable<E> {

    public ObservableSet(Set<E> defValue) {
        super(defValue);
    }

    protected ObservableSet(SettingProperties category, Set<E> defValue) {
        super(category, defValue);
    }

    @Override
    public ImmutableSet<E> get() {
        return (ImmutableSet<E>)super.get();
    }

    @Override
    protected boolean updateUnsafe(Set<E> v) {
        ImmutableSet<E> set = (v == null) ? ImmutableSet.of() : ImmutableSet.copyOf(v);
        return super.updateUnsafe(set);
    }

    public final int size() {
        return get().size();
    }

    protected IOAdapter<Set<E>, ?> makeAdapter(Type[] paramTypes) {
        Type itemType = paramTypes[0];
        return IOAdapter.getImmutableSetAdapter(itemType);
    }

    public HashSet<E> getMutableCopy() {
        return new HashSet<>(super.get());
    }

    @Override
    public Iterator<E> iterator() {
        return super.get().iterator();
    }
}
