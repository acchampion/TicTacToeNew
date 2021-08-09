package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

/**
 * View model class for Contacts, displayed as Strings.
 *
 * Source: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
 *
 * Created by acc on 2021/08/03.
 */
public class ContactViewModel extends AndroidViewModel {

	private final ContactLiveData mAllContacts;

	public ContactViewModel(Application application) {
		super(application);
		mAllContacts = new ContactLiveData(application);
	}

	public ContactLiveData getAllContacts() { return mAllContacts; }

}
