package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.MapsLocationFragment;

public class MapsLocationActivity extends SingleFragmentActivity {

	private final String TAG = getClass().getSimpleName();

	protected Fragment createFragment() {
		return new MapsLocationFragment();
	}


}
