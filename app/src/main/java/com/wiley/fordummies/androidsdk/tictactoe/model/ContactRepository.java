package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;

import com.wiley.fordummies.androidsdk.tictactoe.concurrency.ContactQueryAllCallable;
import com.wiley.fordummies.androidsdk.tictactoe.concurrency.ExecutorRunner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import timber.log.Timber;

public class ContactRepository {

	private final Context mContext;
	private final ExecutorRunner mRunner;
	private final List<Contact> mContactList = new CopyOnWriteArrayList<>();

	private final String TAG = getClass().getSimpleName();

	public ContactRepository(Application application) {
		mContext = application;
		mRunner = new ExecutorRunner();
		loadContacts();
	}

	public List<Contact> getAllContacts() {
		return mContactList;
	}

	private void loadContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		mRunner.execute(new ContactQueryAllCallable(resolver), new ExecutorRunner.Callback<List<Contact>>() {
			@Override
			public void onComplete(List<Contact> contactList) {
				Timber.tag(TAG).d("Got contact list with %d contacts", contactList.size());
				// As this is a background thread, we need to call postValue() to set the
				// value of the LiveData. If we were running on the UI thread, we'd call getValue().
				mContactList.clear();
				mContactList.addAll(contactList);
			}

			@Override
			public void onError(Exception e) {
				Timber.tag(TAG).d("Error retrieving contact list");
				e.printStackTrace();
			}
		});
	}
}
