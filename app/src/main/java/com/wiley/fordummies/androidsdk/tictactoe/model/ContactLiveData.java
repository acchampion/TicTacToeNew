package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.ContentResolver;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.wiley.fordummies.androidsdk.tictactoe.concurrency.ExecutorRunner;

import java.util.List;

import timber.log.Timber;

public class ContactLiveData extends LiveData<List<Contact>> {

	private final Context mContext;
	private final ExecutorRunner mRunner = new ExecutorRunner();

	private final String TAG = getClass().getSimpleName();

	public ContactLiveData(Context context) {
		mContext = context;
		loadContacts();
	}

	private void loadContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		mRunner.execute(new ContactQueryCallable(resolver), new ExecutorRunner.Callback<>() {
			@Override
			public void onComplete(List<Contact> contactList) {
				Timber.tag(TAG).d("Got contact list with %d contacts", contactList.size());
				// As this is a background thread, we need to call postValue() to set the
				// value of the LiveData. If we were running on the UI thread, we'd call getValue().
				postValue(contactList);
			}

			@Override
			public void onError(Exception e) {
				Timber.tag(TAG).d("Error retrieving contact list");
				e.printStackTrace();
			}
		});

	}
}
