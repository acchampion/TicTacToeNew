package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.MediaPlaybackService;
import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.io.File;
import java.util.Objects;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Audio playback Fragment.
 * <p>
 * Created by adamcchampion on 2017/08/12.
 */
public class AudioFragment extends Fragment implements View.OnClickListener {
	private boolean mStarted = false;
	private static final int AUDIO_CAPTURED = 1;
	private Uri mAudioFileUri;
	private final Intent mRecordAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_audio, container, false);

		Button buttonStart = v.findViewById(R.id.buttonAudioStart);
		buttonStart.setOnClickListener(this);
		Button buttonStop = v.findViewById(R.id.buttonAudioStop);
		buttonStop.setOnClickListener(this);
		Button buttonRecord = v.findViewById(R.id.buttonAudioRecord);
		buttonRecord.setOnClickListener(this);

		// Guard against no audio recorder app (disable the "record" button).
		final Activity activity = requireActivity();
		PackageManager packageManager = activity.getPackageManager();
		if (packageManager.resolveActivity(mRecordAudioIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
			buttonRecord.setEnabled(false);
		}

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Activity activity = requireActivity();
		File musicDir = activity.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
		String mAudioFilePath = Objects.requireNonNull(musicDir).getPath() + File.separator + "sample_audio.mp3";

		File audioFile = new File(mAudioFilePath);
		if (audioFile.exists()) {
			mAudioFileUri = Uri.fromFile(audioFile);
		} else {
			// Audio file doesn't exist, so load sample audio from resources.
			String audioResourceName = "android.resource://" + activity.getPackageName() +
					File.separator + R.raw.sample_audio;
			mAudioFileUri = Uri.parse(audioResourceName);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.audio));
			}
		} catch (NullPointerException npe) {
			Timber.e("Could not set subtitle");
		}
	}


	@Override
	public void onClick(View view) {
		final Activity activity = requireActivity();
		final int viewId = view.getId();

		if (viewId == R.id.buttonAudioStart) {
			if (!mStarted) {
				Intent musicIntent = new Intent(activity.getApplicationContext(), MediaPlaybackService.class);
				musicIntent.putExtra("URIString", mAudioFileUri.toString());
				Timber.d("URI: %s", mAudioFileUri.toString());
				activity.startService(musicIntent);
				mStarted = true;
			}
		} else if (viewId == R.id.buttonAudioStop) {
			activity.stopService(new Intent(activity.getApplicationContext(), MediaPlaybackService.class));
			mStarted = false;
		} else if (viewId == R.id.buttonAudioRecord) {
			startActivityForResult(mRecordAudioIntent, AUDIO_CAPTURED);
		} else {
			Timber.e("Invalid button click");
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == AUDIO_CAPTURED) {
			mAudioFileUri = data.getData();
			Timber.v("Audio File URI: %s", mAudioFileUri);
		}
	}
}
