package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

/**
 * Created by acc in August 2023.
 *
 * This creates a "simple" (key, value) PreferencesDataStore using AndroidX Jetpack
 * and RxJava 3. (SharedPreferences is simpler, but DataStores can perform stricter
 * type checking, especially if you use Protocol Buffers to validate "this is an integer",
 * "a string", and so on.)
 *
 * Sources:
 *
 * - <a href="https://medium.com/@deadmanapple/using-the-android-datastore-library-instead-of-sharedpreferences-in-java-d6744c348a05">Maximilian \
 *     Volk's article on Medium</a> (this is very insightful, Singleton pattern)
 * - <a href="https://developer.android.com/topic/libraries/architecture/datastore">Google's
 *     guide to DataStore<a>
 */
public class SettingsDataStoreSingleton {
	static RxDataStore<Preferences> mDataStore;

	private static final SettingsDataStoreSingleton mInstance = new SettingsDataStoreSingleton();

	public static SettingsDataStoreSingleton getInstance(Context context) {
		if (mDataStore == null) {
			mDataStore = new RxPreferenceDataStoreBuilder(context, Settings.NAME).build();
		}
		return mInstance;
	}

	private SettingsDataStoreSingleton() {}

	public void setDataStore(RxDataStore<Preferences> dataStore) {
		mDataStore = dataStore;
	}

	public  RxDataStore<Preferences> getDataStore() {
		return mDataStore;
	}
}
