package org.slowcoders.serialize.android.preference.base;

import android.os.Bundle;

import org.slowcoders.io.settings.ObservableOption;
import org.slowcoders.io.settings.SerializableProperties;
import org.slowcoders.io.settings.SettingProperties;
import org.slowcoders.util.Debug;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

public class SCSettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private SettingProperties properties;
    private int xmlId;

    protected SCSettingFragment(SettingProperties properties, int xmlId) {
        this.properties = properties;
        this.xmlId = xmlId;
    }

    protected SettingProperties getProperties() {
        return this.properties;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceScreen screen = getPreferenceScreen();
        initSettings(screen, properties);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.addPreferencesFromResource(xmlId);
    }

    private void initSettings(PreferenceGroup group, SettingProperties properties) {
        for (int i = 0, count = group.getPreferenceCount(); i < count; i++) {
            Preference pref = group.getPreference(i);
            pref.setPersistent(false);
            if (ISettingPreference.class.isAssignableFrom(pref.getClass())) {
                ((ISettingPreference)pref).initSettings(properties);
                if (pref instanceof PreferenceGroup) {
                    SettingProperties sp = (SettingProperties) properties.getSetting(((SCPreferenceCategory) pref).resolveKey());
                    initSettings((PreferenceGroup) pref, sp);
                } else {
                    pref.setOnPreferenceChangeListener(this);
                }
            }
        }
    }

    protected  <T extends ISettingPreference> T findPreferenceBySetting(ObservableOption option) {
        String key = makeSearchKey(option);
        return (T) super.findPreference(key);
    }

    private String makeSearchKey(ObservableOption option) {
        SettingProperties parent = option.getSerializeInfo().getProperties();
        Debug.Assert(parent != null);
        StringBuilder sb = new StringBuilder();
        while (parent != properties) {
            sb.insert(0, parent.getName() + "/");
            parent = parent.getParentPackage();
        }
        sb.append(option.getSerializeKey());
        return sb.toString();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (ISettingPreference.class.isAssignableFrom(preference.getClass())) {
            ((ISettingPreference)preference).setSettingValue(newValue);
            return true;
        }
        return false;
    }
}
