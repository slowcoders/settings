package org.slowcoders.serialize.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import org.slowcoders.serialize.R;
import org.slowcoders.serialize.android.preference.SettingFragment;
import org.slowcoders.serialize.android.preference.base.SCSettingFragment;
import org.slowcoders.serialize.sample.AndroidSettings;

public class SettingActivity extends AppCompatActivity {

    private SCSettingFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        fragment = SettingFragment.newInstance();

        int layout = R.id.content_pane;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        tr.replace(layout, fragment);
        tr.commit();
    }
}
