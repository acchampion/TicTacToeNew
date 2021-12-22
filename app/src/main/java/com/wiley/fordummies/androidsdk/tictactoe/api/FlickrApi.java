package com.wiley.fordummies.androidsdk.tictactoe.api;

import com.wiley.fordummies.androidsdk.tictactoe.BuildConfig;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FlickrApi {

	@GET("/")
	public Call<String> fetchContents();

	@GET("services/rest/?method=flickr.interestingness.getList" +
		"&api_key=" + BuildConfig.FlickrAccessToken +
	    "&format=json" +
	    "&nojsoncallback=1" +
	    "&extras=url_s")
	public Call<FlickrResponse> fetchPhotos();

	@GET()
	public Call<ResponseBody> fetchUrlBytes(@Url String url);
}
