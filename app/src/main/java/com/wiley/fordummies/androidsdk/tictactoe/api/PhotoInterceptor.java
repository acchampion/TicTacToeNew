package com.wiley.fordummies.androidsdk.tictactoe.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class PhotoInterceptor implements Interceptor {
	private final String TAG = getClass().getSimpleName();

	@NonNull
	@Override
	public Response intercept(@NonNull Chain chain) throws IOException {
		Request originalRequest = chain.request();

		HttpUrl newUrl = originalRequest.url().newBuilder()
				.build();
		Timber.tag(TAG).d("Generated new Url: %s", newUrl);

		Request newRequest = originalRequest.newBuilder()
				.url(newUrl)
				.build();
		Timber.tag(TAG).d("Generated new Request: %s", newRequest);

		return chain.proceed(newRequest);
	}
}
