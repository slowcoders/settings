package org.slowcoders.serialize.android.preference.base;

import android.content.Context;
import android.util.AttributeSet;

import org.slowcoders.io.settings.SettingProperties;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;

public class SCPreferenceCategory extends PreferenceCategory implements ISettingPreference {

    private SettingProperties properties;

    public SCPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SCPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SCPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCPreferenceCategory(Context context) {
        super(context);
    }

    @Override
    public void initSettings(SettingProperties properties) {
        this.properties = properties;
    }

    @Override
    public SettingProperties getSettingProperties() {
        return properties;
    }
}
