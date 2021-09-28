package com.wiley.fordummies.androidsdk.tictactoe.ui;

import androidx.fragment.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {
	@Override
	protected Fragment createFragment() {
		return new PhotoGalleryFragment();
	}
}
