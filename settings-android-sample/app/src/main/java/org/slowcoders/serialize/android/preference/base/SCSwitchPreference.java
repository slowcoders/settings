package org.slowcoders.serialize.android.preference.base;

import android.content.Context;
import android.util.AttributeSet;

import org.slowcoders.io.settings.SettingProperties;

import androidx.preference.SwitchPreference;

public class SCSwitchPreference extends SwitchPreference implements ISettingPreference {

    private SettingProperties properties;

    public SCSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SCSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SCSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCSwitchPreference(Context context) {
        super(context);
    }

    @Override
    public void initSettings(SettingProperties properties) {
        this.properties = properties;
        Boolean v = (Boolean) properties.getProperty(resolveKey());
        super.setChecked(v);
        super.setSummary(v.toString());
    }

    @Override
    public SettingProperties getSettingProperties() {
        return this.properties;
    }
}
