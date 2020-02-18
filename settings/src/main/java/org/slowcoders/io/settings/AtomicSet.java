package org.slowcoders.io.settings;

import com.google.common.collect.ImmutableSet;

import java.util.*;

public class AtomicSet<E> extends ObservableSet<E> implements MutableOption<Set<E>> {

    private ImmutableSet<E> defValue;

    public AtomicSet(Set<E> defValue) {
        this(null, defValue);
    }

    public AtomicSet(SettingProperties properties, Set<E> defValue) {
        super(properties, defValue);
        this.defValue = (ImmutableSet)this.getRawValue();
    }

    @Override
    public ImmutableSet<E> getDefaultValue() {
        return defValue;
    }

    public void resetToDefaultValue() {
        this.set(defValue);
    }

    public boolean set(Set<E> value) {
        return super.set(value);
    }

    public void replaceDefaultValueByCurrentValue() {
        this.defValue = super.get();
    }

    public void addAtomic(E item) {
        HashSet<E> list = this.getMutableCopy();
        list.add(item);
        ((MutableOption)this).set(list);
        return;
    }

    public void removeAtomic(E item) {
        HashSet<E> list = this.getMutableCopy();
        list.remove(item);
        ((MutableOption)this).set(list);
        return;
    }

}
