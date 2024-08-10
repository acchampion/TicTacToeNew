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
	private Button mButtonShowImage, mButtonCaptureImage, mButtonSelectImage;
	private ImageView mImageView = null;
	private String mImageFilePath;
	private final MutableLiveData<Bitmap> mBitmapLiveData = new MutableLiveData<>();
	private Bitmap mBitmap = null;

	private final String TAG = getClass().getSimpleName();

	ActivityResultLauncher<Void> mCapturePhotoLaunch = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(),
			(Bitmap result) -> {
				final int dstWidth = mImageView.getWidth();
				final int dstHeight = mImageView.getHeight();
				Runnable runnable = () -> {
					if (result != null) {
						Bitmap placeholder = BitmapFactory.decodeResource(requireActivity().getResources(), R.drawable.image_placeholder);
						mBitmapLiveData.postValue(placeholder);
						mImageView.setImageBitmap(placeholder);
						mBitmap = Bitmap.createScaledBitmap(result, dstWidth, dstHeight, false);
						mBitmapLiveData.postValue(mBitmap);
						mImageView.setImageBitmap(mBitmap);
					}
				};
				requireActivity().runOnUiThread(runnable);
			});

	ActivityResultLauncher<String> mPickImageResult = registerForActivityResult(new ActivityResultContracts.GetContent(),
			(Uri result) -> {
				final String uriString = result.toString();
				final Uri imageUri = Uri.parse(uriString);
				Runnable runnable = () -> {
					Bitmap placeholder = BitmapFactory.decodeResource(requireActivity().getResources(), R.drawable.image_placeholder);
					mBitmapLiveData.postValue(placeholder);
					mImageView.setImageBitmap(placeholder);
					mBitmap = uriToBitmap(imageUri);
					mBitmapLiveData.postValue(mBitmap);
					mImageView.setImageBitmap(mBitmap);
					mImageView.setContentDescription("Image was set");
				};
				runnable.run();
			});

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_images, container, false);

		mImageView = v.findViewById(R.id.imageView);

		mButtonShowImage = v.findViewById(R.id.buttonImageShow);
		mButtonCaptureImage = v.findViewById(R.id.buttonImageCapture);
		mButtonCaptureImage.setOnClickListener(this);
		mButtonSelectImage = v.findViewById(R.id.buttonImageSelect);
		mButtonSelectImage.setOnClickListener(this);

		return v;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File imageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (imageDir != null) {
            mImageFilePath = imageDir.getPath() + File.separator + "sample_image.jpg";
        }
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

		// Click listener registration
		mButtonShowImage.setOnClickListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mBitmapLiveData.setValue(null);

		if (mBitmap != null) {
			mBitmap.recycle();
		}

		mImageView = null;
		mButtonShowImage = null;
		mButtonSelectImage = null;
		mButtonCaptureImage = null;
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


	/**
	 * Decodes the Bitmap captured by the Camera, and returns the Bitmap. Adapted from Chapter 16
	 * in the "Big Nerd Ranch Guide to Android Development", fourth edition.
	 *
	 * @param selectedFileUri Uri corresponding to the Bitmap to decode
	 * @return The scaled Bitmap for the ImageView
	 */
	private Bitmap uriToBitmap(Uri selectedFileUri) {
		Bitmap image = null;
		ParcelFileDescriptor parcelFileDescriptor = null;

		try {
			Activity activity = requireActivity();
			parcelFileDescriptor = activity.getContentResolver().openFileDescriptor(selectedFileUri, "r");
			if (parcelFileDescriptor != null) {
				FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

				// Get the bounds
				BitmapFactory.Options optionsForBounds = new BitmapFactory.Options();
				optionsForBounds.inJustDecodeBounds = true;

				int dstWidth = mImageView.getWidth();
				int dstHeight = mImageView.getHeight();
				Timber.tag(TAG).d("dstWidth = %d; dstHeight = %d", dstWidth, dstHeight);

				BitmapFactory.decodeFileDescriptor(fileDescriptor, mImageView.getDrawable().getBounds(), optionsForBounds);

				float srcWidth = (float) optionsForBounds.outWidth;
				float srcHeight = (float) optionsForBounds.outHeight;
				Timber.tag(TAG).d("srcWidth = %f; srcHeight = %f", srcWidth, srcHeight);

				int inSampleSize = 1;

				if (srcWidth > dstWidth || srcHeight > dstHeight) {
					float widthScale = srcWidth / dstWidth;
					float heightScale = srcHeight / dstHeight;

					float sampleScale = Math.max(widthScale, heightScale);
					inSampleSize = Math.round(sampleScale);
					Timber.tag(TAG).d("inSampleSize = %d", inSampleSize);
				}

				BitmapFactory.Options actualOptions = new BitmapFactory.Options();
				actualOptions.inSampleSize = inSampleSize;

				image = BitmapFactory.decodeFileDescriptor(fileDescriptor, mImageView.getDrawable().getBounds(), actualOptions);
				// largeBitmap.recycle();
				parcelFileDescriptor.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
