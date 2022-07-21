package com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.wiley.fordummies.androidsdk.tictactoe.model.Contact;
import com.wiley.fordummies.androidsdk.tictactoe.model.ContactLiveData;
import com.wiley.fordummies.androidsdk.tictactoe.model.ContactRepository;

import java.util.List;

/**
 * View model class for Contacts, displayed as Strings.
 *
 * Source: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
 *
 * Created by acc on 2021/08/03.
 */
public class ContactViewModel extends AndroidViewModel {

	private final ContactRepository mRepository;
	private final ContactLiveData mAllContactsData;
	private List<Contact> mAllContactsList;

	public ContactViewModel(Application application) {
		super(application);
		//mAllContacts = new ContactLiveDataOld(application);
		mRepository = new ContactRepository(application);
		mAllContactsData = new ContactLiveData(application);
	}

	public ContactLiveData getAllContacts() {
		mAllContactsList = mRepository.getAllContacts();
		mAllContactsData.setValue(mAllContactsList);

		assert (mAllContactsData.getValue() != null);

		return mAllContactsData;
	}

}
