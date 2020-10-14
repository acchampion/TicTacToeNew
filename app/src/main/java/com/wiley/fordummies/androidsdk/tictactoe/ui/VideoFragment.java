package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.io.File;
import java.util.Objects;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Created by adamcchampion on 2017/08/12.
 */

public class VideoFragment extends Fragment implements View.OnClickListener {
	private VideoView mVideoView = null;
	private Uri mVideoFileUri = null;
	private final Intent mRecordVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);

	private static final int VIDEO_CAPTURED = 1;

	// private static final String TAG = VideoFragment.class.getSimpleName();

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Activity activity = requireActivity();
		View v = inflater.inflate(R.layout.fragment_video, container, false);

		mVideoView = v.findViewById(R.id.videoView);
		Button buttonStart = v.findViewById(R.id.buttonVideoStart);
		buttonStart.setOnClickListener(this);
		Button buttonStop = v.findViewById(R.id.buttonVideoStop);
		buttonStop.setOnClickListener(this);
		Button buttonRecord = v.findViewById(R.id.buttonVideoRecord);
		buttonRecord.setOnClickListener(this);

		// Guard against no audio recorder app (disable the "record" button).
		PackageManager packageManager = activity.getPackageManager();
		if (packageManager.resolveActivity(mRecordVideoIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
			buttonRecord.setEnabled(false);
		}

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Activity activity = requireActivity();
		File videoDir = activity.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
		File videoFile = new File(Objects.requireNonNull(videoDir).getPath() + File.separator + "sample_video.mp4");

		if (Objects.requireNonNull(videoFile).exists()) {
			mVideoFileUri = Uri.fromFile(videoFile);
		} else {
			// Video file doesn't exist, so load sample video from resources.
			String videoResourceName = "android.resource://" + activity.getPackageName() +
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
		} else if (viewId == R.id.buttonVideoStop) {
			mVideoView.stopPlayback();
		} else if (viewId == R.id.buttonVideoRecord) {
			startActivityForResult(mRecordVideoIntent, VIDEO_CAPTURED);
		} else {
			Timber.e("Invalid button press");
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {
			mVideoFileUri = data.getData();
		}
	}
}
