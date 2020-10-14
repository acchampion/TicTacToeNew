package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import timber.log.Timber;

/**
 * Created by adamcchampion on 2017/08/13.
 */

public class SettingsFragment extends PreferenceFragmentCompat  {

   @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load preferences from XML resource.
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();

			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.settings));
			}
		}
        catch (NullPointerException npe) {
            Timber.e("Could not set subtitle");
        }
    }

}
