package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * LiveData class that encapsulates the results of fetching contacts' names.
 *
 *
 *
 * Created by acc on 2021/08/09.
 */
public class ContactLiveData extends LiveData<List<Contact>> {
	private final Context mContext;

	private static final String[] PROJECTION = {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.LOOKUP_KEY,
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
	};

	private final String TAG = getClass().getSimpleName();

	public ContactLiveData(Context context) {
		mContext = context;
		loadContacts();
	}

	@SuppressLint("StaticFieldLeak, Deprecated")
	private void loadContacts() {

		new AsyncTask<Void, Void, List<Contact>>() {
			@Override
			protected List<Contact> doInBackground(Void... voids) {
				List<Contact> contactList = new ArrayList<>();

				ContentResolver resolver = mContext.getContentResolver();
				Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
						PROJECTION,
						null,
						null,
						ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

				try {
					if (cursor != null) {
						cursor.moveToFirst();
						int count = cursor.getCount();
						int position = cursor.getPosition();
						while (position < count) {
							if (cursor.getColumnCount() > 1) {
								int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
								if (nameIndex >= 0) {
									String contactName = cursor.getString(nameIndex);
									Contact contact = new Contact(contactName);
									contactList.add(contact);
									cursor.moveToNext();
									position = cursor.getPosition();
								} else {
									Timber.tag(TAG).e("Invalid column index");
								}
							}
						}
					}
				} finally {
					if (cursor != null && cursor.getCount() > 0) {
						cursor.close();
					}
				}
				return contactList;
			}

			@Override
			protected void onPostExecute(List<Contact> contactList) {
				setValue(contactList);
			}
		}.execute();
	}
}
