package org.slowcoders.io.settings;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class AtomicList<E> extends ObservableList<E> implements MutableOption<List<E>> {

    private ImmutableList<E> defValue;

    public AtomicList(List<E> defValue) {
        this(null, defValue);
    }

    public AtomicList(SettingProperties category, List<E> defValue) {
        super(category, defValue);
        this.defValue = (ImmutableList)this.getRawValue();
    }

    public final ImmutableList<E> getDefaultValue() {
        return this.defValue;
    }

    public void resetToDefaultValue() {
        this.set(defValue);
    }

    public boolean set(List<E> value) {
        return super.set(value);
    }

    public void addAtomic(E item) {
        ArrayList<E> list = this.getMutableCopy();
        list.add(item);
        ((MutableOption)this).set(list);
        return;
    }

    public void removeAtomic(E item) {
        ArrayList<E> list = this.getMutableCopy();
        list.remove(item);
        ((MutableOption)this).set(list);
        return;
    }

    public void replaceDefaultValueByCurrentValue() {
        this.defValue = super.get();
    }

}
