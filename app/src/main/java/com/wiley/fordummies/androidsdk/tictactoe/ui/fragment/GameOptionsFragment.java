package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.wiley.fordummies.androidsdk.tictactoe.MediaPlaybackService;
import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.AudioActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.ContactsActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.GameSessionActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.HelpActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.ImagesActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.MapsActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.PhotoGalleryActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.SensorsActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.SettingsActivity;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.VideoActivity;

import timber.log.Timber;

/**
 * Fragment that handles main user navigation in the app.
 * <p>
 * Created by adamcchampion on 2017/08/05.
 */

public class GameOptionsFragment extends Fragment implements View.OnClickListener {
	private final String TAG = getClass().getSimpleName();

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Timber.tag(TAG).d("onCreateView");
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
		Button btnPhotoGallery = v.findViewById(R.id.buttonPhotoGallery);
		btnPhotoGallery.setOnClickListener(this);
		Button btnExit = v.findViewById(R.id.buttonExit);
		btnExit.setOnClickListener(this);

		setHasOptionsMenu(true);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.options));
			}
		} catch (NullPointerException npe) {
			Timber.tag(TAG).e("Could not set subtitle");
		}
	}

	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu, menu);
	}

	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		final Activity activity = requireActivity();
		final Context appContext = activity.getApplicationContext();
		final int itemId = item.getItemId();

		if (itemId == R.id.menu_settings) {
			startActivity(new Intent(appContext, SettingsActivity.class));
			return true;
		} else if (itemId == R.id.menu_help) {
			startActivity(new Intent(appContext, HelpActivity.class));
			return true;
		} else if (itemId == R.id.menu_exit) {
			showQuitAppDialog();
			return true;
		} else if (itemId == R.id.menu_contacts) {
			startActivity(new Intent(appContext, ContactsActivity.class));
			return true;
		} else {
			Timber.tag(TAG).e("Invalid menu item selection");
			return false;
		}
	}


	public void onClick(View v) {
		final Activity activity = requireActivity();
		final Context appContext = activity.getApplicationContext();
		final int viewId = v.getId();

		if (viewId == R.id.buttonNewGame) {
			startActivity(new Intent(appContext, GameSessionActivity.class));
		} else if (viewId == R.id.buttonAudio) {
			startActivity(new Intent(appContext, AudioActivity.class));
		} else if (viewId == R.id.buttonVideo) {
			startActivity(new Intent(appContext, VideoActivity.class));
		} else if (viewId == R.id.buttonImages) {
			startActivity(new Intent(appContext, ImagesActivity.class));
		} else if (viewId == R.id.buttonMaps) {
			startActivity(new Intent(appContext, MapsActivity.class));
		} else if (viewId == R.id.buttonSettings) {
			startActivity(new Intent(appContext, SettingsActivity.class));
		} else if (viewId == R.id.buttonHelp) {
			startActivity(new Intent(appContext, HelpActivity.class));
		} else if (viewId == R.id.buttonSensors) {
			startActivity(new Intent(appContext, SensorsActivity.class));
		} else if (viewId == R.id.buttonPhotoGallery) {
			startActivity(new Intent(appContext, PhotoGalleryActivity.class));
		} else if (viewId == R.id.buttonExit) {
			activity.stopService(new Intent(appContext, MediaPlaybackService.class));
			showQuitAppDialog();
		} else {
			Timber.tag(TAG).e("Invalid button selection!");
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Activity activity = requireActivity();
		Context appContext = activity.getApplicationContext();
		activity.stopService(new Intent(appContext, MediaPlaybackService.class));
	}

	private void showQuitAppDialog() {
		if (isAdded()) {
			FragmentManager manager = getParentFragmentManager();
			QuitAppDialogFragment fragment = new QuitAppDialogFragment();
			fragment.show(manager, "quit_app");
		} else {
			Timber.tag(TAG).d(TAG, "Fragment not added, quitting!");
			System.exit(0);
		}
	}

}
