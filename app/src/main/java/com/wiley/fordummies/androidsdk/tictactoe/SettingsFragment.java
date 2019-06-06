package com.wiley.fordummies.androidsdk.tictactoe;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import timber.log.Timber;

/**
 * Created by adamcchampion on 2017/08/13.
 */

public class SettingsFragment extends PreferenceFragmentCompat  {
    private final String TAG = getClass().getSimpleName();

   @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load preferences from XML resource.
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppCompatActivity activity = (AppCompatActivity) getActivity();

            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setSubtitle(getResources().getString(R.string.settings));
                }
            }
        }
        catch (NullPointerException npe) {
            Timber.e(TAG, "Could not set subtitle");
        }
    }

}
