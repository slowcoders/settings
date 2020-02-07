package org.slowcoders.io.settings;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class AtomicMap<K, V> extends ObservableMap<K,V> implements MutableOption<Map<K, V>> {

    private ImmutableMap<K, V> defValue;

    public AtomicMap(Map<K, V> defValue) {
        this(null, defValue);
    }

    public AtomicMap(SettingProperties properties, Map<K, V> defValue) {
        super(properties, defValue);
        this.defValue = (ImmutableMap)super.getRawValue();
    }

    @Override
    public ImmutableMap<K, V> getDefaultValue() {
        return defValue;
    }

    @Override
    public void resetToDefaultValue() {
        super.set(this.defValue);
    }

    public void replaceDefaultValueByCurrentValue() {
        this.defValue = super.get();
    }

    public boolean set(Map<K, V> value) {
        return super.set(value);
    }

}
