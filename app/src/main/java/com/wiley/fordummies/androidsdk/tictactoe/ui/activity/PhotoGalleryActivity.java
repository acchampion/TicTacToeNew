package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {
	@Override
	protected Fragment createFragment() {
		return new PhotoGalleryFragment();
	}

	public static Intent newIntent(Context context) {
		return new Intent(context, PhotoGalleryActivity.class);
	}
}
