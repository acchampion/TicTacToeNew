package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;

public class ContactQueryCallable implements Callable<List<Contact>> {

	private final ContentResolver mResolver;
	private static final String[] PROJECTION = {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.LOOKUP_KEY,
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
	};

	private final String TAG = getClass().getSimpleName();

	public ContactQueryCallable(ContentResolver resolver) {
		mResolver = resolver;
	}

	@Override
	public List<Contact> call() {
		List<Contact> contactList = new ArrayList<>();

		Cursor cursor = mResolver.query(ContactsContract.Contacts.CONTENT_URI,
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
}
