package org.slowcoders.serialize.android.preference.base;

import org.slowcoders.io.settings.MutableOption;
import org.slowcoders.io.settings.ObservableOption;
import org.slowcoders.io.settings.SettingProperties;

public interface ISettingPreference {

    void initSettings(SettingProperties properties);

    SettingProperties getSettingProperties();

    String getKey();

    default String resolveKey() {
        String key = getKey();
        int i = key.lastIndexOf("/");
        if (i < 0) {
            return key;
        }
        return key.substring(i + 1);
    }

    default void setSettingValue(Object value) {
        SettingProperties properties = getSettingProperties();
        ObservableOption option = properties.findOption(resolveKey());
        if (option instanceof MutableOption) {
            ((MutableOption)option).set(value);
        }
    }
}
