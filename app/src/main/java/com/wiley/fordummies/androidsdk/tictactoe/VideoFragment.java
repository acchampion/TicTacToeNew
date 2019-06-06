package com.wiley.fordummies.androidsdk.tictactoe;

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

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Created by adamcchampion on 2017/08/12.
 */

public class VideoFragment extends Fragment implements View.OnClickListener {
    private VideoView mVideoView = null;
    private Uri mVideoFileUri = null;
    private Intent mRecordVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);

    private static final int VIDEO_CAPTURED = 1;

    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_video, container, false);

        mVideoView = v.findViewById(R.id.videoView);

        Button buttonStart = v.findViewById(R.id.buttonVideoStart);
        buttonStart.setOnClickListener(this);
        Button buttonStop = v.findViewById(R.id.buttonVideoStop);
        buttonStop.setOnClickListener(this);
        Button buttonRecord = v.findViewById(R.id.buttonVideoRecord);
        buttonRecord.setOnClickListener(this);

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath() +
                File.separator + "sample_video.mp4";
        File videoFile = new File(path);
        if (videoFile.exists()) {
            mVideoFileUri = Uri.fromFile(videoFile);
        } else {
            // Video file doesn't exist, so load sample video from resources.
            if (activity != null) {
                String videoResourceName = "android.resource://" + activity.getPackageName() +
                        File.separator + R.raw.sample_video;
                mVideoFileUri = Uri.parse(videoResourceName);
            }
        }

        // Guard against no audio recorder app (disable the "record" button).
        if (activity != null) {
            PackageManager packageManager = activity.getPackageManager();
            if (packageManager.resolveActivity(mRecordVideoIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
                buttonRecord.setEnabled(false);
            }
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppCompatActivity activity = (AppCompatActivity) getActivity();

            if (activity != null) {
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setSubtitle(getResources().getString(R.string.video));
                }
            }
        } catch (NullPointerException npe) {
            Timber.e(TAG, "Could not set subtitle");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonVideoStart:
                // Load and start the movie
                mVideoView.setVideoURI(mVideoFileUri);
                mVideoView.start();
                break;
            case R.id.buttonVideoRecord:
                startActivityForResult(mRecordVideoIntent, VIDEO_CAPTURED);
                break;
            case R.id.buttonVideoStop:
                mVideoView.stopPlayback();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED) {
            mVideoFileUri = data.getData();
        }
    }
}
