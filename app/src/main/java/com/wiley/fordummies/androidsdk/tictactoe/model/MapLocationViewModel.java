package com.wiley.fordummies.androidsdk.tictactoe.model;

import androidx.lifecycle.ViewModel;

public class MapLocationViewModel extends ViewModel {

	private String mLocation;

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}
}
