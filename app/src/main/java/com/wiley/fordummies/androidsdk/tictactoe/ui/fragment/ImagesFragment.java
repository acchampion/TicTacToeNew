package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
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
import java.io.FileDescriptor;
import java.io.IOException;

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
			(Bitmap result) -> {
				Runnable runnable = () -> {
					mBitmapLiveData.postValue(result);
					Bitmap bitmap = mBitmapLiveData.getValue();
					mImageView.setImageBitmap(bitmap);
				};
				runnable.run();
			});

	ActivityResultLauncher<String> mPickImageResult = registerForActivityResult(new ActivityResultContracts.GetContent(),
			(Uri result) -> {
				final String uriString = result.toString();
				final Uri imageUri = Uri.parse(uriString);
				Runnable runnable = () -> {
					Bitmap bitmap = uriToBitmap(imageUri);
					mBitmapLiveData.postValue(bitmap);
					mImageView.setImageURI(imageUri);
				};
				runnable.run();
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
	public void onDestroyView() {
		super.onDestroyView();
		mBitmapLiveData.setValue(null);
		mImageView = null;
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

	private Bitmap uriToBitmap(Uri selectedFileUri) {
		Bitmap image = null;
		try {
			Activity activity = requireActivity();
			ParcelFileDescriptor parcelFileDescriptor =
					activity.getContentResolver().openFileDescriptor(selectedFileUri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

			parcelFileDescriptor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
