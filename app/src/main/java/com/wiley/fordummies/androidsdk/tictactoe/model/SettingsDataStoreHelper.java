package com.wiley.fordummies.androidsdk.tictactoe.model;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesFactory;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

/**
 * Created by acc in August 2023.
 * <p>
 * This creates a "simple" (key, value) PreferencesDataStore using AndroidX Jetpack
 * and RxJava 3.
 *
 * Sources:
 * <p>
 * - <a href="https://medium.com/@deadmanapple/using-the-android-datastore-library-instead-of-sharedpreferences-in-java-d6744c348a05">Maximilian \
 * Volk's article on Medium</a> (this is very insightful, Singleton pattern)
 * - <a href="https://developer.android.com/topic/libraries/architecture/datastore">Google's
 * guide to DataStore<a>
 */
public class SettingsDataStoreHelper {
	private final RxDataStore<Preferences> mDataStore;

	private final Preferences.Key<String> PREF_KEY_ERROR = PreferencesKeys.stringKey(Settings.ERROR);
	private final Preferences errorPrefs = PreferencesFactory.create(PREF_KEY_ERROR.to("true"));

	private final String TAG = getClass().getSimpleName();

	public SettingsDataStoreHelper(RxDataStore<Preferences> dataStore) {
		mDataStore = dataStore;
	}

	public int getInt(String key, int defaultValue) {
		final Preferences.Key<Integer> PREF_KEY = PreferencesKeys.intKey(key);
		Single<Integer> value = mDataStore.data()
				.firstOrError()
				.map(prefs -> prefs.get(PREF_KEY))
				.onErrorReturnItem(defaultValue);
		Timber.tag(TAG).i("getInt(): DataStore data: %s", mDataStore.data().map(Preferences::asMap));
		final int val = value.blockingGet();
		Timber.tag(TAG).d("getInt(): key %s, value %d", key, val);
		return val;
	}

	public boolean putInt(String key, int value) {
		boolean returnVal;
		final Preferences.Key<Integer> PREF_KEY = PreferencesKeys.intKey(key);

		Single<Preferences> updateResult = mDataStore.updateDataAsync(prefsIn -> {
			MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
			mutablePrefs.set(PREF_KEY, value);
			Timber.tag(TAG + " RxJava lambda:").d("Writing int (key, val) (%s, %d)", key, value);
			return Single.just(mutablePrefs);
		}).onErrorReturnItem(errorPrefs);
		Preferences returnPrefs = updateResult.blockingGet();
		Timber.tag(TAG).i("Result prefs = %s", returnPrefs);
		Timber.tag(TAG).d("Error prefs version = %s", errorPrefs);
		returnVal = !returnPrefs.equals(errorPrefs);
		Timber.tag(TAG).d("putInt(): key %s, value %d, success: %s", PREF_KEY, value, returnVal);
		return returnVal;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		final Preferences.Key<Boolean> PREF_KEY = PreferencesKeys.booleanKey(key);
		Single<Boolean> value = mDataStore.data()
				.firstOrError()
				.map(prefs -> prefs.get(PREF_KEY))
				.onErrorReturnItem(defaultValue);
		Timber.tag(TAG).i("getBoolean(): DataStore data: %s", mDataStore.data().map(Preferences::asMap));
		final boolean val = value.blockingGet();
		Timber.tag(TAG).d("getBoolean(): key %s, value %s", key, val);
		return val;
	}

	public boolean putBoolean(String key, boolean value) {
		boolean returnVal;
		final Preferences.Key<Boolean> PREF_KEY = PreferencesKeys.booleanKey(key);

		Single<Preferences> updateResult = mDataStore.updateDataAsync(prefsIn -> {
			MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
			mutablePrefs.set(PREF_KEY, value);
			Timber.tag(TAG + " RxJava lambda:").d("Writing boolean (key, val) (%s, %s)", key, value);
			return Single.just(mutablePrefs);
		}).onErrorReturnItem(errorPrefs);
		Preferences returnPrefs = updateResult.blockingGet();
		Timber.tag(TAG).i("Result prefs = %s", returnPrefs);
		Timber.tag(TAG).d("Error prefs version = %s", errorPrefs);
		returnVal = !returnPrefs.equals(errorPrefs);
		Timber.tag(TAG).d("putBoolean(): key %s, value %s, success: %s", PREF_KEY, value, returnVal);
		return returnVal;
	}

	public String getString(String key, String defaultValue) {
		final Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(key);
		Single<String> value = mDataStore.data()
				.firstOrError()
				.map(prefs -> prefs.get(PREF_KEY))
				.onErrorReturnItem(defaultValue);
		Timber.tag(TAG).i("DataStore data: %s", mDataStore.data().map(prefs -> prefs.get(PREF_KEY)).toList());
		final String val = value.blockingGet();
		Timber.tag(TAG).d("getString(): key %s, value %s", key, val);
		return val;
	}

	public boolean putString(String key, String value) {
		boolean returnVal;
		final Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(key);

		Single<Preferences> updateResult = mDataStore.updateDataAsync(prefsIn -> {
			MutablePreferences mutablePrefs = prefsIn.toMutablePreferences();
			mutablePrefs.set(PREF_KEY, value);
			Timber.tag(TAG + " RxJava lambda:").d("Writing String (key, val) (%s, %s)", key, value);
			return Single.just(mutablePrefs);
		}).onErrorReturnItem(errorPrefs);
		Preferences returnPrefs = updateResult.blockingGet();
		Timber.tag(TAG).i("Result prefs = %s", returnPrefs);
		Timber.tag(TAG).d("Error prefs version = %s", errorPrefs);
		returnVal = !returnPrefs.equals(errorPrefs);
		Timber.tag(TAG).d("putString(): key %s, value %s, success: %s", PREF_KEY, value, returnVal);
		return returnVal;
	}
}
