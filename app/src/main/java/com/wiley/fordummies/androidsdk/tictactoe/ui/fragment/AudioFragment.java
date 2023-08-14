package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.MediaPlaybackService;
import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.io.File;

import timber.log.Timber;

/**
 * Audio playback Fragment.
 *
 * Now includes ActivityResultLaunchers.
 *
 * Created by adamcchampion on 2017/08/12.
 */
public class AudioFragment extends Fragment implements View.OnClickListener {
	private boolean mStarted = false;
	private Uri mAudioFileUri;
	private final Intent mRecordAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
	private Button mButtonStart, mButtonStop;

	ActivityResultLauncher<Intent> mRecordAudioResult = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (result.getResultCode() == RESULT_OK) {
						Intent intent = result.getData();
						if (intent != null) {
							mAudioFileUri = intent.getData();
							Timber.tag(TAG).v("Audio File URI: %s", mAudioFileUri);
						}
					}
				}
			});

	ActivityResultLauncher<String> mPickAudioResult = registerForActivityResult(
			new ActivityResultContracts.GetContent(),
			new ActivityResultCallback<Uri>() {
				@Override
				public void onActivityResult(Uri result) {
					if (result != null) {
						String uriString = result.toString();
						mAudioFileUri = Uri.parse(uriString);
					}
				}
			});

	private final String TAG = getClass().getSimpleName();

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_audio, container, false);

		mButtonStart = v.findViewById(R.id.buttonAudioStart);
		mButtonStart.setOnClickListener(this);
		mButtonStop = v.findViewById(R.id.buttonAudioStop);
		mButtonStop.setOnClickListener(this);
		Button buttonRecord = v.findViewById(R.id.buttonAudioRecord);
		buttonRecord.setOnClickListener(this);
		Button buttonSelect = v.findViewById(R.id.buttonAudioSelect);
		buttonSelect.setOnClickListener(this);

		// Guard against no audio recorder app (disable the "record" button).
		/* final Activity activity = requireActivity();
		PackageManager packageManager = activity.getPackageManager();
		if (packageManager.resolveActivity(mRecordAudioIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
			buttonRecord.setEnabled(false);
		} */

		return v;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context ctx = requireContext();
		final File musicDir = ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
		final String mAudioFilePath = musicDir.getPath() + File.separator + "sample_audio.mp3";

		File audioFile = new File(mAudioFilePath);
		if (audioFile.exists()) {
			mAudioFileUri = Uri.fromFile(audioFile);
		} else {
			// Audio file doesn't exist, so load sample audio from resources.
			final String audioResourceName = "android.resource://" + ctx.getPackageName() +
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
			Timber.tag(TAG).e("Could not set subtitle");
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Timber.tag(TAG).d("onDestroyView()");
		mButtonStart = null;
		mButtonStop = null;
	}

	@Override
	public void onClick(View view) {
		final Activity activity = requireActivity();
		final int viewId = view.getId();

		if (viewId == R.id.buttonAudioStart) {
			if (!mStarted) {
				Intent musicIntent = new Intent(activity.getApplicationContext(), MediaPlaybackService.class);
				musicIntent.putExtra("URIString", mAudioFileUri.toString());
				Timber.tag(TAG).d("URI: %s", mAudioFileUri.toString());
				activity.startService(musicIntent);
				mStarted = true;

				mButtonStart.setEnabled(false);
				mButtonStop.setEnabled(true);
			}
		} else if (viewId == R.id.buttonAudioStop) {
			activity.stopService(new Intent(activity.getApplicationContext(), MediaPlaybackService.class));
			mStarted = false;

			mButtonStart.setEnabled(true);
			mButtonStop.setEnabled(false);
		} else if (viewId == R.id.buttonAudioRecord) {
			mRecordAudioResult.launch(new Intent(mRecordAudioIntent));
		} else if (viewId == R.id.buttonAudioSelect) {
			mPickAudioResult.launch("audio/*");
		} else {
			Timber.tag(TAG).e("Invalid button click");
		}
	}

}
