package org.slowcoders.io.settings;

import com.google.common.collect.ImmutableMap;
import org.slowcoders.io.serialize.IOAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ObservableMap<K, V> extends ObservableOption<Map<K, V>> {

    public ObservableMap(Map<K, V> defValue) {
        super(defValue);
    }

    protected ObservableMap(SettingProperties category, Map<K, V> defValue) {
        super(category, defValue);
    }


    @Override
    public ImmutableMap<K, V> get() {
        return (ImmutableMap<K, V>)super.get();
    }

    @Override
    protected boolean updateUnsafe(Map<K, V> v) {
        ImmutableMap map = (v == null) ? ImmutableMap.of() : ImmutableMap.copyOf(v);
        return super.updateUnsafe(map);
    }

    public final int size() {
        return super.get().size();
    }

    public V get(K key) {
        return super.get().get(key);
    }

    public void put(K key, V value) {
        HashMap<K, V> map = new HashMap<>();
        map.putAll(super.get());

        map.put(key, value);

        this.set(map);
    }

    public V remove(K key) {
        HashMap<K, V> map = new HashMap<>();
        map.putAll(super.get());

        V v = map.remove(key);

        this.set(map);

        return v;
    }

    public HashMap<K,V> getMutableCopy() {
        return new HashMap<>(super.get());
    }


    protected IOAdapter<Map<K,V>, ?> makeAdapter(Type[] types) {
        return IOAdapter.getImmutableMapAdapter(types[0], types[1]);
    }
}
