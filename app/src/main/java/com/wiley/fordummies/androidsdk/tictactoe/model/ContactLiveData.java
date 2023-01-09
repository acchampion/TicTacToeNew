package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import timber.log.Timber;

public class ContactLiveData extends MutableLiveData<List<Contact>> {

	private final String TAG = getClass().getSimpleName();

	public ContactLiveData(Context context) {
		Timber.tag(TAG).d("In ContactLiveData() constructor");
	}

}
