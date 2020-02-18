package org.slowcoders.serialize.android.preference;

import android.os.Bundle;

import org.slowcoders.io.settings.AtomicOption;
import org.slowcoders.serialize.R;
import org.slowcoders.serialize.android.preference.base.SCDialogPreference;
import org.slowcoders.serialize.android.preference.base.SCEditTextPreference;
import org.slowcoders.serialize.android.preference.base.SCSettingFragment;
import org.slowcoders.serialize.android.preference.base.SCSwitchPreference;
import org.slowcoders.serialize.sample.AndroidSettings;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class SettingFragment extends SCSettingFragment {

    private AndroidSettings setting;

    private SCDialogPreference dialogPreference;
    private SCEditTextPreference dependentEditText;

    private SCSwitchPreference dependentSwitch;

    private SettingFragment() {
        super(AndroidSettings.pref, R.xml.setting_preference);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setting = (AndroidSettings) getProperties();

        this.dialogPreference = findPreferenceBySetting(setting.dialogPreference);
        this.dependentEditText = findPreferenceBySetting(setting.observablePreference.dependentEditText);
        this.dependentSwitch = findPreferenceBySetting(setting.dependentSwitch);

        if (setting.observablePreference.observableSwitch.get()) {
            this.dependentEditText.setEnabled(false);
        } else {
            this.dependentEditText.setEnabled(true);
        }

        /**
         *  add observer to setting option.
         *  we get notified whenever it changes.
         */
        this.setting.observablePreference.observableSwitch.addAsyncObserver(v -> {
            if (v) {
                this.dependentEditText.setEnabled(false);
            } else {
                this.dependentEditText.setEnabled(true);
            }
        });

        /**
         *  add observer to SettingProperties.
         *  we get notified whenever subordinate options change.
         */
        this.setting.observableCategory.addAsyncObserver(v -> {
            this.dependentSwitch.setSummary("Data has been updated!!!");
            this.dependentSwitch.setChecked(false);
        });
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary(newValue.toString());
        return super.onPreferenceChange(preference, newValue);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference == dialogPreference) {
                PreferenceDialogFragmentCompat dialog = dialogPreference.createDialog(
                        (key, option) -> new PersonDialogFragment(key, (AtomicOption<AndroidSettings.Person>)option)
            );
            dialog.setTargetFragment(this, 0);
            dialog.show(getFragmentManager(), null);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

}
