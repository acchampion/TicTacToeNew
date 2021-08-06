package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

/**
 * View model class for Contacts, displayed as Strings.
 *
 * Source: https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
 *
 * Created by acc on 2021/08/03.
 */
public class ContactViewModel extends AndroidViewModel {
	private final MutableLiveData<List<String>> mListData = new MutableLiveData<>();

	public ContactViewModel(Application application) {
		super(application);

	}

	private void loadData() {
		new AsyncTask<Void, Void, List<String>>() {

			@Override
			protected List<String> doInBackground(Void... voids) {
				return null;
			}
		}.execute();
	}
}
