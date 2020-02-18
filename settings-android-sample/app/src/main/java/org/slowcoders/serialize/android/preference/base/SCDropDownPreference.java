package org.slowcoders.serialize.android.preference.base;

import android.content.Context;
import android.util.AttributeSet;

import org.slowcoders.io.settings.SettingProperties;

import androidx.preference.DropDownPreference;

public class SCDropDownPreference extends DropDownPreference implements ISettingPreference {

    private SettingProperties properties;

    public SCDropDownPreference(Context context) {
        super(context);
    }

    public SCDropDownPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCDropDownPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SCDropDownPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
        return this.properties;
    }
}
