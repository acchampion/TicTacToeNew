package com.wiley.fordummies.androidsdk.tictactoe.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface FlickrApi {

	@GET("/")
	Call<String> fetchContents();

	@GET("services/rest/?method=flickr.interestingness.getList" +
	    "&format=json" +
	    "&nojsoncallback=1" +
	    "&extras=url_s")
	Call<FlickrResponse> fetchPhotos(@Query("api_key") String query);

	@GET()
	Call<ResponseBody> fetchUrlBytes(@Url String url);

	@GET("services/rest/?method=flickr.photos.search")
	Call<FlickrResponse> searchPhotos(@Query("text") String query);
}
