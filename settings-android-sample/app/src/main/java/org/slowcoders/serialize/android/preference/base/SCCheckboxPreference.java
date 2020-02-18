package org.slowcoders.serialize.android.preference.base;

import android.content.Context;
import android.util.AttributeSet;

import org.slowcoders.io.settings.SettingProperties;

import androidx.preference.CheckBoxPreference;

public class SCCheckboxPreference extends CheckBoxPreference implements ISettingPreference {

    private SettingProperties properties;

    public SCCheckboxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SCCheckboxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SCCheckboxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCCheckboxPreference(Context context) {
        super(context);
    }

    @Override
    public void initSettings(SettingProperties properties) {
        this.properties = properties;
        Boolean v = (Boolean) properties.getProperty(resolveKey());
        super.setChecked(v);
        super.setSummary(v.toString());
    }

    public SettingProperties getSettingProperties() {
        return properties;
    }

}
