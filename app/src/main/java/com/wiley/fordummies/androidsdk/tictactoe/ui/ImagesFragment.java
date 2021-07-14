package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.io.File;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment for showing and capturing images.
 * <p>
 * Created by adamcchampion on 2017/08/12.
 */

public class ImagesFragment extends Fragment implements View.OnClickListener {
	private ImageView mImageView = null;
	private final static int IMAGE_CAPTURED = 1;
	private String mImageFilePath;
	private Bitmap mImageBitmap = null;
	private final Intent mCaptureImageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_images, container, false);

		mImageView = v.findViewById(R.id.imageView);

		Button buttonShow = v.findViewById(R.id.buttonImageShow);
		buttonShow.setOnClickListener(this);
		Button buttonCapture = v.findViewById(R.id.buttonImageCapture);
		buttonCapture.setOnClickListener(this);

		// Guard against no camera app (disable the "record" button).
		Activity activity = requireActivity();
		PackageManager packageManager = activity.getPackageManager();
		if (packageManager.resolveActivity(mCaptureImageIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
			buttonCapture.setEnabled(false);
		}

		return v;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File imageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		mImageFilePath = imageDir.getPath() + File.separator + "sample_image.jpg";
	}

	@Override
	public void onClick(View view) {
		final int viewId = view.getId();

		if (viewId == R.id.buttonImageShow) {
			File imageFile = new File(mImageFilePath);
			if (imageFile.exists()) {
				mImageBitmap = BitmapFactory.decodeFile(mImageFilePath);
				mImageView.setImageBitmap(mImageBitmap);
			} else {
				// File doesn't exist, so load a sample SVG image.
				// Disable hardware acceleration for SVGs
				mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				mImageView.setImageResource(R.drawable.ic_scoreboard);
			}
		} else if (viewId == R.id.buttonImageCapture) {
			startActivityForResult(mCaptureImageIntent, IMAGE_CAPTURED);
		} else {
			Timber.e("Invalid button click");
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent cameraIntent) {
		if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURED) {
			Bundle extras = cameraIntent.getExtras();
			if (extras != null) {
				mImageBitmap = (Bitmap) extras.get("data");
				mImageView.setImageBitmap(mImageBitmap);
			}
		}
	}

	private void takeImage() {
		Lifecycle lifecycle = getLifecycle();

	}

}
