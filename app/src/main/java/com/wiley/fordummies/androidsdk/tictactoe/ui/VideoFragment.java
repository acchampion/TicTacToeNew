package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.io.File;
import java.util.Objects;

import timber.log.Timber;

/**
 * Created by adamcchampion on 2017/08/12.
 */

public class VideoFragment extends Fragment implements View.OnClickListener {
	private VideoView mVideoView;
	private Uri mVideoFileUri;
	private Button mButtonStart, mButtonStop;
	private final Intent mRecordVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);

	private static final int VIDEO_CAPTURED = 1;

	private static final String TAG = VideoFragment.class.getSimpleName();

	ActivityResultLauncher<Intent> mCaptureVideoResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (result.getResultCode() == Activity.RESULT_OK) {
						Intent intent = result.getData();
						if (intent != null) {
							mVideoFileUri = intent.getData();
							mVideoView.setVideoURI(mVideoFileUri);
							Timber.v("Audio File URI: %s", mVideoFileUri);
						}
					}
				}
			});

	ActivityResultLauncher<String> mPickVideoResult = registerForActivityResult(new ActivityResultContracts.GetContent(),
			new ActivityResultCallback<Uri>() {
				@Override
				public void onActivityResult(Uri result) {
					String uriString = result.toString();
					mVideoFileUri  = Uri.parse(uriString);
					mVideoView.setVideoURI(mVideoFileUri);
				}
			});

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Activity activity = requireActivity();
		View v = inflater.inflate(R.layout.fragment_video, container, false);

		mVideoView = v.findViewById(R.id.videoView);
		mButtonStart = v.findViewById(R.id.buttonVideoStart);
		mButtonStart.setOnClickListener(this);
		mButtonStop = v.findViewById(R.id.buttonVideoStop);
		mButtonStop.setOnClickListener(this);
		Button buttonRecord = v.findViewById(R.id.buttonVideoRecord);
		buttonRecord.setOnClickListener(this);
		Button buttonSelect = v.findViewById(R.id.buttonVideoSelect);
		buttonSelect.setOnClickListener(this);

//		// Guard against no video recorder app (disable the "record" button).
//		PackageManager packageManager = activity.getPackageManager();
//		if (packageManager.resolveActivity(mRecordVideoIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
//			buttonRecord.setEnabled(false);
//		}

		return v;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context ctx = requireContext();
		File videoDir = ctx.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
		final String videoFilePath = videoDir.getPath() + File.separator + "sample_video.mp4";
		File videoFile = new File(videoFilePath);

		if (Objects.requireNonNull(videoFile).exists()) {
			mVideoFileUri = Uri.fromFile(videoFile);
		} else {
			// Video file doesn't exist, so load sample video from resources.
			final String videoResourceName = "android.resource://" + ctx.getPackageName() +
					File.separator + R.raw.sample_video;
			mVideoFileUri = Uri.parse(videoResourceName);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();

			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.video));
			}
		} catch (NullPointerException npe) {
			Timber.e("Could not set subtitle");
		}
	}

	@Override
	public void onClick(View view) {
		final int viewId = view.getId();

		if (viewId == R.id.buttonVideoStart) {
			// Load and start the movie
			mVideoView.setVideoURI(mVideoFileUri);
			mVideoView.start();
			// Make the start button inactive and the stop button active
			mButtonStart.setEnabled(false);
			mButtonStop.setEnabled(true);
		} else if (viewId == R.id.buttonVideoStop) {
			mVideoView.stopPlayback();
			// Make the start button active and the stop button inactive
			mButtonStart.setEnabled(true);
			mButtonStop.setEnabled(false);
		} else if (viewId == R.id.buttonVideoRecord) {
			mCaptureVideoResult.launch(mRecordVideoIntent);
		} else if (viewId == R.id.buttonVideoSelect) {
			mPickVideoResult.launch("video/*");
		} else {
			Timber.e("Invalid button press");
		}
	}
}
