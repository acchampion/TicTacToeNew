package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import timber.log.Timber;

/**
 * Fragment that handles main user navigation in the app.
 *
 * Created by adamcchampion on 2017/08/05.
 */

public class GameOptionsFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_options, container, false);

        Button btnNewGame = v.findViewById(R.id.buttonNewGame);
        btnNewGame.setOnClickListener(this);
        Button btnAudio = v.findViewById(R.id.buttonAudio);
        btnAudio.setOnClickListener(this);
        Button btnVideo = v.findViewById(R.id.buttonVideo);
        btnVideo.setOnClickListener(this);
        Button btnImage = v.findViewById(R.id.buttonImages);
        btnImage.setOnClickListener(this);
        Button btnMaps = v.findViewById(R.id.buttonMaps);
        btnMaps.setOnClickListener(this);
        Button btnSettings = v.findViewById(R.id.buttonSettings);
        btnSettings.setOnClickListener(this);
        Button btnHelp = v.findViewById(R.id.buttonHelp);
        btnHelp.setOnClickListener(this);
        Button btnTestSensors = v.findViewById(R.id.buttonSensors);
        btnTestSensors.setOnClickListener(this);
        Button btnExit = v.findViewById(R.id.buttonExit);
        btnExit.setOnClickListener(this);

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setSubtitle(getResources().getString(R.string.options));

                }
            }
        }
        catch (NullPointerException npe) {
            Timber.e(TAG, "Could not set subtitle");
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Activity activity = getActivity();

        if (activity != null) {
            switch (item.getItemId()) {
                case R.id.menu_settings:
                    startActivity(new Intent(activity.getApplicationContext(), SettingsActivity.class));
                    return true;
                case R.id.menu_help:
                    startActivity(new Intent(activity.getApplicationContext(), HelpActivity.class));
                    return true;
                case R.id.menu_exit:
                    showQuitAppDialog();
                    return true;
                case R.id.menu_contacts:
                    startActivity(new Intent(activity.getApplicationContext(), ContactsActivity.class));
                    return true;
            }
        }
        return false;
    }


    public void onClick(View v) {
        Activity activity = getActivity();

        if (activity != null) {
            switch (v.getId()) {
                case R.id.buttonNewGame:
                    startActivity(new Intent(activity.getApplicationContext(), GameSessionActivity.class));
                    break;
                case R.id.buttonAudio:
                    startActivity(new Intent(activity.getApplicationContext(), AudioActivity.class));
                    break;
                case R.id.buttonVideo:
                    startActivity(new Intent(activity.getApplicationContext(), VideoActivity.class));
                    break;
                case R.id.buttonImages:
                    startActivity(new Intent(activity.getApplicationContext(), ImagesActivity.class));
                    break;
                case R.id.buttonMaps:
                    startActivity(new Intent(activity.getApplicationContext(), MapsActivity.class));
                    break;
                case R.id.buttonSettings:
                    startActivity(new Intent(activity.getApplicationContext(), SettingsActivity.class));
                    break;
                case R.id.buttonHelp:
                    startActivity(new Intent(activity.getApplicationContext(), HelpActivity.class));
                    break;
                case R.id.buttonSensors:
                    startActivity(new Intent(activity.getApplicationContext(), SensorsActivity.class));
                    break;
                case R.id.buttonExit: {
                    getActivity().stopService(new Intent(activity.getApplicationContext(), MediaPlaybackService.class));
                    showQuitAppDialog();
                }
                break;
            }
        }
    }

    private void showQuitAppDialog() {
        FragmentManager manager = getFragmentManager();
        QuitAppDialogFragment fragment = new QuitAppDialogFragment();

        if (manager != null) {
            fragment.show(manager, "quit_app");
        }
    }

}
