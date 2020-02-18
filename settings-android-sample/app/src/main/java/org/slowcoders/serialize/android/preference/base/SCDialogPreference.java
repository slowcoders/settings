package org.slowcoders.serialize.android.preference.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.slowcoders.io.settings.AtomicOption;
import org.slowcoders.io.settings.ObservableOption;
import org.slowcoders.io.settings.SettingProperties;
import org.slowcoders.serialize.R;
import org.slowcoders.serialize.sample.AndroidSettings;

import java.util.ArrayList;
import java.util.List;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class SCDialogPreference extends DialogPreference implements ISettingPreference {

    private SettingProperties properties;

    public SCDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SCDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SCDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCDialogPreference(Context context) {
        super(context);
    }

    @Override
    public void initSettings(SettingProperties properties) {
        this.properties = properties;
    }

    @Override
    public SettingProperties getSettingProperties() {
        return this.properties;
    }

    public PreferenceDialogFragmentCompat createDialog(DialogFactory factory) {
        return factory.createDialog(getKey(), properties.findOption(resolveKey()));
    }

    public interface DialogFactory {
        PreferenceDialogFragmentCompat createDialog(String key, ObservableOption option);
    }

}
