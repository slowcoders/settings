package org.slowcoders.serialize.android.preference.base;

import android.content.Context;
import android.util.AttributeSet;

import org.slowcoders.io.settings.SettingProperties;

import java.util.Set;

import androidx.preference.MultiSelectListPreference;

public class SCMultiSelectListPreference extends MultiSelectListPreference implements ISettingPreference {

    private SettingProperties properties;

    public SCMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SCMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SCMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCMultiSelectListPreference(Context context) {
        super(context);
    }

    @Override
    public void initSettings(SettingProperties properties) {
        this.properties = properties;
        Set<String> set = (Set<String>) properties.getProperty(resolveKey());
        super.setValues(set);
    }

    @Override
    public SettingProperties getSettingProperties() {
        return this.properties;
    }
}
