package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {
	@Override
	protected Fragment createFragment() {
		return new PhotoGalleryFragment();
	}
}
