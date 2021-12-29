package com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.wiley.fordummies.androidsdk.tictactoe.network.FlickrFetchr;
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;
import com.wiley.fordummies.androidsdk.tictactoe.model.QueryPreferences;

import java.util.List;

public class PhotoGalleryViewModel extends AndroidViewModel {
	public final LiveData<List<GalleryItem>> mGalleryItemLiveData;
	private final Application mApplication;

	private final FlickrFetchr mFlickrFetchr = new FlickrFetchr();
	private final MutableLiveData<String> mMutableSearchTerm = new MutableLiveData<>();

	public PhotoGalleryViewModel(Application application) {
		super(application);
		mApplication = application;
		mMutableSearchTerm.setValue(QueryPreferences.getStoredQuery(mApplication));

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
		QueryPreferences.setStoredQuery(mApplication, query);
		mMutableSearchTerm.setValue(query);
	}

	public void fetchPhotos() {
		fetchPhotos("");
	}
}
