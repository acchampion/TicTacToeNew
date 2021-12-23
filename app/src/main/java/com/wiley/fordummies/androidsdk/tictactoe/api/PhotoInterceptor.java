package com.wiley.fordummies.androidsdk.tictactoe.api;

import androidx.annotation.NonNull;

import com.wiley.fordummies.androidsdk.tictactoe.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PhotoInterceptor implements Interceptor {
	private final String API_KEY = BuildConfig.FlickrAccessToken;

	@NonNull
	@Override
	public Response intercept(@NonNull Chain chain) throws IOException {
		Request originalRequest = chain.request();

		HttpUrl newUrl = originalRequest.url().newBuilder()
				.addQueryParameter("api_key", API_KEY)
				.addQueryParameter("format", "json")
				.addQueryParameter("nojsoncallback", "1")
				.addQueryParameter("extras", "url_s")
				.addQueryParameter("safesearch", "1")
				.build();

		Request newRequest = originalRequest.newBuilder()
				.url(newUrl)
				.build();

		return chain.proceed(newRequest);
	}
}
