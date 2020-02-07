package org.slowcoders.io.settings;

import org.slowcoders.util.Debug;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SettingProperties extends SerializableProperties<SettingProperties.Observer> {

    private List<ObservableOption> options = new ArrayList<>();
    private final ArrayList<SerializableProperties> propertiesList = new ArrayList<>();
    private final List<SerializableProperties> unmodifiableList = Collections.unmodifiableList(propertiesList);
    private static final String[] gEmptyStringArray = new String[0];



    public interface Observer {
        void onPropertyChanged(SettingProperties properties);
    }

    public class _Option<E> extends AtomicOption<E> {
        public _Option(E defValue) {
            super(SettingProperties.this, defValue);
        }
    }

    public class _List<E> extends AtomicList<E> {
        public _List(List<E> defValue) {
            super(SettingProperties.this, defValue);
        }
    }

    public class _Map<K,V> extends AtomicMap<K,V> {
        public _Map(Map<K,V> defValue) {
            super(SettingProperties.this, defValue);
        }
    }

    public class _Set<E> extends AtomicSet<E> {
        public _Set(java.util.Set<E> defValue) {
            super(SettingProperties.this, defValue);
        }
    }


    public SettingProperties(SettingProperties parent) {
        this(parent, null);
    }

    public SettingProperties(SettingProperties parent, String name) {
        super(parent, name);
        if (parent != null) {
            parent.addSetting(this);
        }
    }

    public void setProperties(SettingProperties properties) {
        if (properties == this) return;

        //@msg.Jonghoon.To_Daehoon("init 함수 추가")
        properties.init(false);
        for (ObservableOption option : properties.options) {
            String name = option.getSerializeInfo().getName();
            ObservableOption op2 = this.findOption(name);
            if (op2 != null) {
                op2.set(option.get());
            }
        }
    }

    public void save() throws Exception {
        SettingModule module = this.getModule();
        String storePath = module.getStorePath(this);
        exportTo(module.getStorage(), storePath);
    }

    public void reload() throws Exception {
        boolean notifyPropertyChange = !this.init(false);
        getModule().load(this, notifyPropertyChange);

        for (SerializableProperties properties : propertiesList) {
            properties.reload();
        }

    }


    public void exportTo(SettingStorage store, String storePath) throws Exception {
        this.init(true);
        store.save(this, storePath);

        for (SerializableProperties properties : propertiesList) {
            properties.exportTo(store, storePath + '/' + properties.getName());
        }

    }


//    public ObservableOption findOption(String name) {
//        this.init(false);
//
//        if (name.charAt(0) == '/') {
//            super.getRootModule().findOption(name);
//        }
//        for (ObservableOption option : options) {
//            String key = option.getSerializeInfo().getName();
//            if (key.equals(name)) {
//                return option;
//            }
//        }
//        return null;
//    }


    public Iterable<ObservableOption> options() {
        return this.options;
    }

    public void resetAllOptions() {
//        @msg.Jonghoon.To_Daehoon("init() 함수 추가")
        init(false);
        for (ObservableOption opt : options){
            if (opt instanceof MutableOption) {
                ((MutableOption)opt).resetToDefaultValue();
            }
            else {
                Debug.trap();
            }
        }
    }

    @Deprecated
    public void replaceDefaultPropertiesByCurrentSettings() {
        for (ObservableOption opt : options){
            if (opt instanceof MutableOption) {
                ((MutableOption)opt).replaceDefaultValueByCurrentValue();
            }
            else {
                Debug.trap();
            }
        }
    }


    @Override
    protected void doNotify(Observer observer, Object data) {
        observer.onPropertyChanged(this);
    }

    final void addOption(ObservableOption option){
        options.add(option);
    }

    protected synchronized boolean init(boolean loadProperties) {
        if (this.options.size() != 0) {
            return false;
        }

        Class c = this.getClass();

        try {
            while (SettingProperties.class != c) {
                init_internal(c);
                c = c.getSuperclass();
            }
        }
        catch (Exception e){
            Debug.fatal(e);
        }

        if (loadProperties) {
            getModule().load(this, false);
        }
        return true;
    }

    private void init_internal(Class c) throws IllegalAccessException {

        for (Field field : c.getDeclaredFields()) {

            if (!ObservableOption.class.isAssignableFrom(field.getType())
                    &&  !SettingProperties.class.isAssignableFrom(field.getType())) {
                continue;
            }
            if (SettingProperties.class.isAssignableFrom(field.getType())
            ||  SettingProperties.class.isAssignableFrom(field.getType())) {
                continue;
            }

            if ((field.getModifiers() & Modifier.FINAL) == 0
                    || (field.getModifiers() & Modifier.STATIC) != 0) {
                throw Debug.wtf("SharedOption must be a final field: " + field);
            }

            if ((field.getModifiers() & Modifier.PRIVATE) != 0){
                field.setAccessible(true);
            }

            Object p = field.get(this);
            if (field.getName().equals("stringValue")) {
                Debug.trap();
            }

            if (p instanceof ObservableOption) {
                field.setAccessible(true);
                ObservableOption option = (ObservableOption) p;
                option.getRawSerializeInfo().init(field, option);
                this.addOption(option);
            }
        }
    }



//    public void exportTo(SettingStorage store, String storePath) throws Exception {
//        for (SerializableProperties properties : propertiesList) {
//            properties.exportTo(store, storePath + '/' + properties.getName());
//        }
//    }
//
//    public void reload() throws Exception {
//        for (SerializableProperties properties : propertiesList) {
//            properties.reload();
//        }
//    }

    public final ObservableOption findOption(String key) {
        this.init(true);
        return this.findRawOption(key);
    }

    protected ObservableOption findRawOption(String key) {
        SettingProperties pack = this;
        switch (key.charAt(0)) {
            case '/':
                key = key.substring(1);
                pack = getRootModule();
                break;

            case '.':
                while (true) {
                    int next_char = key.charAt(1);
                    if (next_char == '/') {
                        key = key.substring(2);
                    } else if (next_char == '.' && key.charAt(2) == '/') {
                        key = key.substring(3);
                        pack = this.getParentPackage();
                    }
                    if (key.charAt(0) != '.') {
                        break;
                    }
                }

            default:
                for (ObservableOption option : options) {
                    String name = option.getSerializeInfo().getName();
                    if (key.equals(name)) {
                        return option;
                    }
                }
                break;
        }

        if (pack != this) {
            Debug.Assert(key.charAt(0) != '/');
            return pack.findOption(key);
        }

        for (int i = propertiesList.size(); --i >= 0; ) {
            SerializableProperties settings = propertiesList.get(i);
            if (key.startsWith(settings.getName())) {
                String subKey = key.substring(settings.getName().length() + 1);
                return settings.findOption(subKey);
            }
        }
        return null;
    }



    public final SerializableProperties getSetting(String name) {
        for (int i = propertiesList.size(); --i >= 0; ) {
            SerializableProperties settings = propertiesList.get(i);
            if (name.startsWith(settings.getName())) {
                return settings;
            }
        }
        return null;
    }

    protected void addSetting(SettingProperties properties) {
        synchronized (propertiesList) {
            String rookie_name = properties.getName();
            int i = propertiesList.size();
            for (; --i >= 0; ) {
                SerializableProperties p = propertiesList.get(i);
                int diff = p.getName().compareToIgnoreCase(rookie_name);
                if (diff < 0) {
                    break;
                }
                if (diff == 0) {
                    throw Debug.wtf("Same setting-file is already registered: " + properties.getName());
                }
            }
            propertiesList.add(i + 1, properties);
        }
        super.postNotification(this);
    }

    protected boolean removeSetting(SerializableProperties properties) {
        synchronized (propertiesList) {
            if (!propertiesList.remove(properties)) {
                return false;
            };
        }
        super.postNotification(this);
        return true;
    }

    public Iterable<SerializableProperties> subSettings() {
        return propertiesList;
    }

    public String[] listStoredSettings() {
        String[] fileNames = getModule().listStoredSettings(this);
        if (fileNames == null) {
            fileNames = gEmptyStringArray;
        }
        return fileNames;
    }

    public List<? extends SerializableProperties> getChildSettings() {
        return unmodifiableList;
    }
}