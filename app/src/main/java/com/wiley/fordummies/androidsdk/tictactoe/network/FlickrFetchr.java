package com.wiley.fordummies.androidsdk.tictactoe.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wiley.fordummies.androidsdk.tictactoe.api.FlickrApi;
import com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse;
import com.wiley.fordummies.androidsdk.tictactoe.api.PhotoInterceptor;
import com.wiley.fordummies.androidsdk.tictactoe.api.PhotoResponse;
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class FlickrFetchr {

	private final PhotoInterceptor mPhotoInterceptor;
	private final OkHttpClient mClient;
	private final FlickrApi mFlickrApi;
	private final Retrofit mRetrofit;

	private final String TAG = getClass().getSimpleName();

	public FlickrFetchr() {
		mPhotoInterceptor = new PhotoInterceptor();
		mClient = new OkHttpClient.Builder()
				.addInterceptor(mPhotoInterceptor)
				.build();

		mRetrofit = new Retrofit.Builder()
				.baseUrl("https://api.flickr.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.client(mClient)
				.build();

		mFlickrApi = mRetrofit.create(FlickrApi.class);
	}

	public Call<FlickrResponse> fetchPhotosRequest() {
		return mFlickrApi.fetchPhotos();
	}

	public LiveData<List<GalleryItem>> fetchPhotosOld() {
		MutableLiveData<List<GalleryItem>> responseLiveData = new MutableLiveData<>();
		Call<FlickrResponse> flickrRequest = mFlickrApi.fetchPhotos();

		flickrRequest.enqueue(new Callback<FlickrResponse>() {
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

	@WorkerThread
	public Bitmap fetchPhoto(String url) {
		Bitmap bitmap = null;
		InputStream inputStream = null;
		try {
			Response<ResponseBody> response = mFlickrApi.fetchUrlBytes(url).execute();
			try (ResponseBody body = response.body()) {
				if (body != null) {
					inputStream = body.byteStream();
					bitmap = BitmapFactory.decodeStream(inputStream);
					Timber.tag(TAG).i("Decoded bitmap " + bitmap + " from Response " + response);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bitmap;
	}

	public LiveData<List<GalleryItem>> fetchPhotos() {
		// return fetchPhotoMetadata(mFlickrApi.fetchPhotos());
		return fetchPhotoMetadata(fetchPhotosRequest());
	}

	public Call<FlickrResponse> searchPhotosRequest(String query) {
		return mFlickrApi.searchPhotos(query);
	}

	public LiveData<List<GalleryItem>> searchPhotos(String query) {
		// return fetchPhotoMetadata(mFlickrApi.searchPhotos(query));
		return fetchPhotoMetadata(searchPhotosRequest(query));
	}

	private LiveData<List<GalleryItem>> fetchPhotoMetadata(Call<FlickrResponse> flickrRequest) {
		MutableLiveData<List<GalleryItem>> responseLiveData = new MutableLiveData<>();
		flickrRequest.enqueue(new Callback<FlickrResponse>() {
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

