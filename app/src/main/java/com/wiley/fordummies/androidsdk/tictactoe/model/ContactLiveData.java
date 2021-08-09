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

/**
 * LiveData class that encapsulates the results of fetching contacts' names.
 *
 *
 *
 * Created by accc on 2021/08/09.
 */
public class ContactLiveData extends LiveData<List<Contact>> {
	private final Context mContext;

	private static final String[] PROJECTION = {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.LOOKUP_KEY,
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
	};


	/*
	 * Defines an array that contains column names to move from
	 * the Cursor to the ListView.
	 */
	private final static String[] FROM_COLUMNS = {
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
	};


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
								String contactName = cursor.getString(cursor.getColumnIndex("display_name"));
								Contact contact = new Contact(contactName);
								contactList.add(contact);
								cursor.moveToNext();
								position = cursor.getPosition();
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
