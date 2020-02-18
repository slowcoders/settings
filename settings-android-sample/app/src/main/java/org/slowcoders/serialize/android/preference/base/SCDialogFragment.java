package org.slowcoders.serialize.android.preference.base;

import android.os.Bundle;

import org.slowcoders.io.settings.ObservableOption;

import androidx.preference.PreferenceDialogFragmentCompat;

public abstract class SCDialogFragment extends PreferenceDialogFragmentCompat {

    private ObservableOption option;

    protected SCDialogFragment(String key, ObservableOption option) {
        this.option = option;

        Bundle b = new Bundle();
        b.putString(ARG_KEY, key);
        setArguments(b);
    }

    protected ObservableOption getOption() {
        return this.option;
    }
}
