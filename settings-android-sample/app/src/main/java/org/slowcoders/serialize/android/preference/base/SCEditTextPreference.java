package org.slowcoders.serialize.android.preference.base;

import android.content.Context;
import android.util.AttributeSet;

import org.slowcoders.io.settings.SettingProperties;

import androidx.preference.EditTextPreference;

public class SCEditTextPreference extends EditTextPreference implements ISettingPreference {

    private SettingProperties properties;

    public SCEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SCEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SCEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCEditTextPreference(Context context) {
        super(context);
    }

    @Override
    public void initSettings(SettingProperties properties) {
        this.properties = properties;
        String v = (String) properties.getProperty(resolveKey());
        super.setSummary(v);
        super.setText(v);
    }

    @Override
    public SettingProperties getSettingProperties() {
        return properties;
    }
}
