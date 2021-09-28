package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.io.File;

import timber.log.Timber;

/**
 * Fragment for showing and capturing images.
 * <p>
 * Created by adamcchampion on 2017/08/12.
 */

public class ImagesFragment extends Fragment implements View.OnClickListener {
	private ImageView mImageView = null;
	private String mImageFilePath;
	private final MutableLiveData<Bitmap> mBitmapLiveData = new MutableLiveData<>();

	private final String TAG = getClass().getSimpleName();

	ActivityResultLauncher<Void> mCapturePhotoLaunch = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(),
			result -> {
				mBitmapLiveData.setValue(result);
				mImageView.setImageBitmap(mBitmapLiveData.getValue());
			});

	ActivityResultLauncher<String> mPickImageResult = registerForActivityResult(new ActivityResultContracts.GetContent(),
			result -> {
				String uriString = result.toString();
				Uri imageUri = Uri.parse(uriString);
				mImageView.setImageURI(imageUri);
			});

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_images, container, false);

		mImageView = v.findViewById(R.id.imageView);

		Button buttonShow = v.findViewById(R.id.buttonImageShow);
		buttonShow.setOnClickListener(this);
		Button buttonCapture = v.findViewById(R.id.buttonImageCapture);
		buttonCapture.setOnClickListener(this);
		Button buttonSelect = v.findViewById(R.id.buttonImageSelect);
		buttonSelect.setOnClickListener(this);

		return v;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File imageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		mImageFilePath = imageDir.getPath() + File.separator + "sample_image.jpg";
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.images));
			}
		} catch (NullPointerException npe) {
			Timber.tag(TAG).e("Could not set subtitle");
		}
	}


	@Override
	public void onClick(View view) {
		final int viewId = view.getId();

		if (viewId == R.id.buttonImageShow) {
			File imageFile = new File(mImageFilePath);
			if (imageFile.exists()) {
				Bitmap imageBitmap = BitmapFactory.decodeFile(mImageFilePath);
				mImageView.setImageBitmap(imageBitmap);
			} else {
				// File doesn't exist, so load a sample SVG image.
				// Disable hardware acceleration for SVGs
				mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				mImageView.setImageResource(R.drawable.ic_scoreboard);
			}
		} else if (viewId == R.id.buttonImageCapture) {
			mCapturePhotoLaunch.launch(null);
		} else if (viewId == R.id.buttonImageSelect) {
			mPickImageResult.launch("image/*");
		} else {
			Timber.tag(TAG).e("Invalid button click");
		}
	}
}
