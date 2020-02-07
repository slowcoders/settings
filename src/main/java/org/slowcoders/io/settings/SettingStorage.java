package org.slowcoders.io.settings;

public interface SettingStorage {

    void load(SettingProperties settings, String storagePath, boolean notifyPropertyChange);

    void save(SettingProperties settings, String storagePath);

    void delete(String storagePath);

    String[] listSettings(String storagePath);


    SettingStorage dummyStorage = new SettingStorage() {
        @Override
        public void load(SettingProperties settings, String storagePath, boolean notifyPropertyChange) {
            return;
        }

        @Override
        public void save(SettingProperties settings, String storagePath) {
            return;
        }

        @Override
        public void delete(String storagePath) {
            return;
        }

        @Override
        public String[] listSettings(String storagePath) {
            return new String[0];
        }
    };

}
