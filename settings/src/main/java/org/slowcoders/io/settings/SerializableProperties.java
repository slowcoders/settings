package org.slowcoders.io.settings;

import org.slowcoders.io.serialize.IOEntity;
import org.slowcoders.io.serialize.IOField;
import org.slowcoders.util.Debug;
import org.slowcoders.observable.AsyncObservable;

import java.util.HashMap;
import java.util.Map;

public abstract class SerializableProperties<Observer> extends AsyncObservable<Observer> {

    private final SettingProperties parent;
    private final String name;

    public SerializableProperties(SettingProperties storage, String name) {
        if (name == null) {
            name = this.getClass().getSimpleName();
        }
        this.parent = storage;
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public SettingModule getModule() {
        return parent.getModule();
    }

    public final SettingProperties getRootModule() {
        SerializableProperties p, node = this;
        while ((p = node.getParentPackage()) != null) {
            node = p;
        }
        return node.asModule();
    }

    public final SettingProperties getParentPackage() {
        return parent;
    }

    public abstract void reload() throws Exception;

    public abstract void exportTo(SettingStorage store, String storePath) throws Exception;


    public void delete() {
        this.getModule().delete(this);
        getParentPackage().removeSetting(this);
    }

    public final void copyTo(SettingProperties destFolder) throws Exception {
        SettingModule module = destFolder.getModule();
        String path = module.getStorePath(destFolder) + '/' + this.getName();
        exportTo(module.getStorage(), path);
    }

    public String getOptionKey(ObservableOption property) {
        SerializeInfo si = property.getSerializeInfo();
        SerializableProperties node = si.properties;
        if (node == this) {
            return si.getName();
        }

        StringBuilder sb = new StringBuilder();
        this.getStoreKey(node, sb);
        sb.append('/').append(si.getName());
        return sb.toString();
    }

    final void getStoreKey(SerializableProperties node, StringBuilder sb) {
        for (; node != this; node = node.parent) {
            if (sb.length() > 0) {
                sb.insert(0, '/');
            }
            sb.insert(0, node.name);
        }
    }

    public SettingModule asModule() { return null; }

    public abstract ObservableOption findOption(String key);

    public Object getProperty(String key) {
        Object value = fetchProperty(key, null, null);
        return value;
    }

    public void setProperty(String key, Object value) {
        MutableOption option = (MutableOption)findOption(key);
        option.set(value);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    final synchronized Object fetchProperty(String key, HashMap<ObservableOption, Object> mutableCache, Object newValue) {
        boolean doUpdate = mutableCache != null;

        ObservableOption option;
        int optionKey_len = key.lastIndexOf('?');
        String optionKey = (optionKey_len < 0) ? key : key.substring(0, optionKey_len);

        option = (ObservableOption)this.findOption(optionKey);

        if (option == null) {
            throw new IllegalArgumentException("invalid property name: " + key);
        }

        if (optionKey == key) {
            if (!doUpdate) {
                return option.get();
            }
            else {
                mutableCache.put(option, newValue);
                return null;
            }
        }

        try {
            Object entity = option.get();
            int key_start = optionKey.length() + 1;
            String name = key.substring(key_start);
            IOField[] fields = IOEntity.registerSerializableFields(entity.getClass());
            IOField field = IOEntity.getSerializableFieldByName(name, fields);
            Object value = field.getReflectionField().get(entity);
            if (doUpdate) {
                boolean isChanged = value == null ? newValue != null : !value.equals(newValue);
                if (isChanged) {
                    entity = mutableCache.get(option);
                    if (entity == null) {
                        entity = option.getMutableCopy();
                        mutableCache.put(option, entity);
                    }
                    field.getReflectionField().set(entity, newValue);
                }
            }
            return value;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("invalid property name: " + key);
        }
    }

    public Editor edit() {
        return new Editor(this);
    }


    public static class Editor {

        private HashMap<ObservableOption, Object> editMap = new HashMap<>();
        private SerializableProperties settings;


        public Editor(SerializableProperties settingStorage) {
            this.settings = settingStorage;
        }

        public synchronized void setProperty(String key, Object value) {
            settings.fetchProperty(key, editMap, value);
        }

        public synchronized <T> void setProperty(AtomicOption<T> property, T value) {
            editMap.put(property, value);
        }

        @SuppressWarnings("rawtypes")
        public void commit() {

            HashMap<String, SettingProperties> modifiedCategories = new HashMap<>();

            for (Map.Entry<ObservableOption, Object> e : editMap.entrySet()) {
                ObservableOption option = e.getKey();
                Object value = e.getValue();
                if (option.set(value)) {
                    SettingProperties cat = option.getSerializeInfo().getProperties();
                    modifiedCategories.put(cat.getModule().getStorePath(cat), cat);
                }
            }

            try {
                for (SettingProperties p : modifiedCategories.values()) {
                    p.save();
                }
            }
            catch (Exception e) {
                throw Debug.fatal(e);
            }

            this.editMap.clear();
        }
    }

    public String toString() {
        return this.getName();
    }


}