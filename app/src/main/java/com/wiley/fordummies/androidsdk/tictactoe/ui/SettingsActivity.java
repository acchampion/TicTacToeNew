package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;

/**
 * Created by adamcchampion on 2017/08/13.
 */

public class SettingsActivity extends AppCompatActivity {

    protected Fragment createFragment() {
        return new SettingsFragment();
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }
 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            Fragment preferenceFragment = createFragment();
            fm.beginTransaction()
                    .replace(R.id.fragment_container, preferenceFragment)
                    .commit();
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

}
