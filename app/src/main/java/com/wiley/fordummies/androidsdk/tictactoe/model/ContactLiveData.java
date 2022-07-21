package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.wiley.fordummies.androidsdk.tictactoe.concurrency.ExecutorRunner;

import java.util.List;

public class ContactLiveData extends MutableLiveData<List<Contact>> {

	private final Context mContext;
	private final ExecutorRunner mRunner = new ExecutorRunner();

	private final String TAG = getClass().getSimpleName();

	public ContactLiveData(Context context) {
		mContext = context;
	}

}
