package com.wiley.fordummies.androidsdk.tictactoe.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wiley.fordummies.androidsdk.tictactoe.api.FlickrApi;
import com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse;
import com.wiley.fordummies.androidsdk.tictactoe.api.PhotoResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class FlickrFetchr {

	private FlickrApi mFlickrApi;
	private Retrofit mRetrofit;

	private final String TAG = getClass().getSimpleName();

	public FlickrFetchr() {
		mRetrofit = new Retrofit.Builder()
				.baseUrl("https://api.flickr.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		mFlickrApi = mRetrofit.create(FlickrApi.class);
	}

	public LiveData<List<GalleryItem>> fetchPhotos() {
		MutableLiveData<List<GalleryItem>> responseLiveData = new MutableLiveData<>();
		Call<FlickrResponse> flickrRequest = mFlickrApi.fetchPhotos();

		flickrRequest.enqueue(new Callback<>() {
			@Override
			public void onResponse(@NonNull Call<FlickrResponse> call, @NonNull Response<FlickrResponse> response) {
				Timber.tag(TAG).d("Response received");
				FlickrResponse flickrResponse = response.body();
				if (flickrResponse != null) {
					PhotoResponse photoResponse = flickrResponse.getPhotos();
					List<GalleryItem> galleryItems = photoResponse.getGalleryItems();
					List<GalleryItem> validGalleryItems = new ArrayList<>();
					for (GalleryItem item: galleryItems) {
						if (item.getUrl() != null) {
							validGalleryItems.add(item);
						}
					}
					responseLiveData.setValue(validGalleryItems);
				}
			}

			@Override
			public void onFailure(@NonNull Call<FlickrResponse> call, @NonNull Throwable t) {
				Timber.tag(TAG).e(t, "Failed to fetch photos");
			}
		});

		return responseLiveData;
	}
}

