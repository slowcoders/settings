package org.slowcoders.serialize.android.preference.base;

import android.content.Context;
import android.util.AttributeSet;

import org.slowcoders.io.settings.SettingProperties;

import androidx.preference.ListPreference;

public class SCListPreference extends ListPreference implements ISettingPreference {

    private SettingProperties properties;

    public SCListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SCListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SCListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCListPreference(Context context) {
        super(context);
    }

    @Override
    public void initSettings(SettingProperties properties) {
        this.properties = properties;
        String v = (String) properties.getProperty(resolveKey());
        super.setValue(v);
        super.setSummary(v);
    }

    @Override
    public SettingProperties getSettingProperties() {
        return properties;
    }
}
