package org.slowcoders.io.settings;

import java.util.ArrayList;
import java.util.List;

public class SettingModule extends SettingProperties {

    private final SettingStorage storage;

    public SettingModule(SettingStorage storage, SettingProperties parent, String name) {
        super(parent, name);
        this.storage = storage;
    }

    public SettingModule(SettingStorage storage, String name) {
        this(storage, null, name);
    }

    public SettingModule(SettingStorage storage) {
        this(storage, null, "");
    }

    public final SettingStorage getStorage() {
        return this.storage;
    }

    @Override
    public SettingModule asModule() { return this; }

    @Override
    public SettingModule getModule() {
        return this;
    }

    protected void delete(SerializableProperties settings) {
        String path = getStorePath(settings);
        storage.delete(path);
    }

    protected final String getStorePath(SerializableProperties node) {
        if (node == this) {
            return this.getName();
        }
        StringBuilder sb = new StringBuilder();
        this.getStoreKey(node, sb);
        if (sb.length() > 0) {
            sb.insert(0, '/');
        }
        sb.insert(0, this.getName());
        return sb.toString();
    }

    protected String[] listStoredSettings(SettingProperties settings) {
        String path = getStorePath(settings);
        return storage.listSettings(path);
    }

    public void load(SettingProperties settings, boolean notifyPropertyChange) {
        String storePath = getStorePath(settings);
        storage.load(settings, storePath, notifyPropertyChange);
    }

    protected final <T extends SettingProperties> List<T> loadChildSettings(SettingsFactory<T> factory) {
        ArrayList<T> lists = new ArrayList<>();
        String[] strings = listStoredSettings(this);
        if (strings != null) {
            for(String name : strings) {
                T settings = factory.createSettings(this, name.substring(0, name.lastIndexOf(".")));
                lists.add(settings);
            }
        }
        return lists;
    }

    public boolean removeSetting(SerializableProperties settings) {
        if (super.removeSetting(settings)){
            String storePath = getStorePath(settings);
            storage.delete(storePath);
            return true;
        }
        return false;
    }

    public interface SettingsFactory<T extends SettingProperties> {

        public T createSettings(SettingModule settingModule, String name);
    }

//    protected void save(SerializableProperties settings, SettingPackage destFolder) {
//        String path = this.getStorePath(destFolder) + '/' + settings.getName();
//        settings.init(true);
//        storage.save(settings, path);;
//    }
}
