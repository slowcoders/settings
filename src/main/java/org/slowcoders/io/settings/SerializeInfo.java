package org.slowcoders.io.settings;

import org.slowcoders.io.serialize.IOAdapter;
import org.slowcoders.io.serialize.IOCtrl;
import org.slowcoders.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public final class SerializeInfo {
    final ObservableOption property;
    final SettingProperties properties;
    IOAdapter adapter;
    int flags;
    String key;
    Field field;

    SerializeInfo(ObservableOption property, SettingProperties properties) {
        this.properties = properties;
        this.property = property;
        field = null;
    }

    SerializeInfo() {
        this.property = null;
        this.properties = null;
        this.flags = SettingFlags.NotAutoSave;
        this.adapter = IOAdapter.getDefaultAdapter(String.class);
    }

    public final SettingProperties getProperties() {
        return this.properties;
    }

    public final String getName() {
        return this.key;
    }

    final void init() {
        if (this.adapter == null) {
            properties.init(true);
        }
    }

    void init(Field field, ObservableOption property) {
        Type type = field.getGenericType();
        Type paramTypes[] = ClassUtils.getGenericParameters(type);
        IOCtrl ioctrl = field.getAnnotation(IOCtrl.class);

        String key = field.getName();
        IOAdapter adapter = null;
        int flags = 0;

        if (ioctrl != null) {
            if (ioctrl.key() != null) key = ioctrl.key();
            flags = ioctrl.flags();
            adapter = IOAdapter.getAdapterInstance(ioctrl.adapter());
        }

        if (adapter == null) {
            adapter = property.makeAdapter(paramTypes);
        }

        this.key = key;
        this.flags = flags;
        this.adapter = adapter;
        this.field = field;
    }

    final IOAdapter<Object, Object> getAdapter() {
        return this.adapter;
    }

    public final boolean getFlag_AutoSave() {
        return (this.flags & SettingFlags.NotAutoSave) == 0;
    }
}
