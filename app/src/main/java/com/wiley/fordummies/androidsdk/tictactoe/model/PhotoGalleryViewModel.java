package com.wiley.fordummies.androidsdk.tictactoe.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PhotoGalleryViewModel extends ViewModel {
	public final LiveData<List<GalleryItem>> mGalleryItemLiveData;

	public PhotoGalleryViewModel() {
		FlickrFetchr flickrFetchr = new FlickrFetchr();
		mGalleryItemLiveData = flickrFetchr.fetchPhotos();
	}

	public LiveData<List<GalleryItem>> getGalleryItemLiveData() {
		return mGalleryItemLiveData;
	}
}
