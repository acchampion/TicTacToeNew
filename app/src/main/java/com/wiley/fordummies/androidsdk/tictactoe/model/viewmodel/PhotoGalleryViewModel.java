package com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel;

import android.app.Application;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;
import com.wiley.fordummies.androidsdk.tictactoe.model.QueryPreferences;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStoreHelper;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStoreSingleton;
import com.wiley.fordummies.androidsdk.tictactoe.network.FlickrFetchr;

import java.util.List;

import timber.log.Timber;

public class PhotoGalleryViewModel extends AndroidViewModel {
	public final LiveData<List<GalleryItem>> mGalleryItemLiveData;
	private final Application mApplication;


	private final SettingsDataStoreSingleton mDataStoreSingleton;
	private final SettingsDataStoreHelper mDataStoreHelper;

	private final FlickrFetchr mFlickrFetchr = new FlickrFetchr();
	private final MutableLiveData<String> mMutableSearchTerm = new MutableLiveData<>();

	private final String TAG = getClass().getSimpleName();

	public PhotoGalleryViewModel(Application application) {
		super(application);
		mApplication = application;
		mDataStoreSingleton = SettingsDataStoreSingleton.getInstance(mApplication);
		RxDataStore<Preferences> mDataStore = mDataStoreSingleton.getDataStore();
		mDataStoreHelper = new SettingsDataStoreHelper(mDataStore);

		mMutableSearchTerm.setValue(mDataStoreHelper.getString(QueryPreferences.PREF_SEARCH_QUERY, ""));

		// mGalleryItemLiveData = flickrFetchr.fetchPhotosOld();
		mGalleryItemLiveData = Transformations.switchMap(mMutableSearchTerm, (String searchTerm) ->
				{
					if (searchTerm.isEmpty()) {
						return mFlickrFetchr.fetchPhotos();
					} else {
						return mFlickrFetchr.searchPhotos(searchTerm);
					}
				}
		);
	}

	public String getSearchTerm() {
		return mMutableSearchTerm.getValue();
	}

	public LiveData<List<GalleryItem>> getGalleryItemLiveData() {
		return mGalleryItemLiveData;
	}

	public void fetchPhotos(String query) {
		if (mDataStoreHelper.putString(QueryPreferences.PREF_SEARCH_QUERY, query)) {
			Timber.tag(TAG).i("Stored query %s successfully", query);
		} else {
			Timber.tag(TAG).e("Error storing search query %s", query);
		}
		mMutableSearchTerm.setValue(query);
	}

	public void fetchPhotos() {
		fetchPhotos("");
	}
}
