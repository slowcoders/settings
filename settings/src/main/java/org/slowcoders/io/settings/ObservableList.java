package org.slowcoders.io.settings;

import com.google.common.collect.ImmutableList;
import org.slowcoders.io.serialize.IOAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObservableList<E> extends ObservableOption<List<E>> implements Iterable<E>{

    public ObservableList(List<E> defValue) {
        this(null, defValue);
    }

    public ObservableList(SettingProperties category, List<E> defValue) {
        super(category, defValue);
    }

    public final int size() {
        return super.get().size();
    }

    public ImmutableList<E> get() {
        return (ImmutableList<E>)super.get();
    }

    protected boolean updateUnsafe(List<E> v) {
        ImmutableList list = (v == null) ? ImmutableList.of() : ImmutableList.copyOf(v);
        return super.updateUnsafe(list);
    }

    protected IOAdapter<List<E>, ?> makeAdapter(Type[] paramTypes) {
        Type itemType = paramTypes[0];
        return IOAdapter.getImmutableListAdapter(itemType);
    }

    public ArrayList<E> getMutableCopy() {
        return new ArrayList<>(super.get());
    }

    @Override
    public Iterator<E> iterator() {
        return super.get().iterator();
    }

}
