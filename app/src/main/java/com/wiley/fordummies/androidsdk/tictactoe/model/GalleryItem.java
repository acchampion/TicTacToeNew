package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;

import java.util.Objects;

@JsonClass(generateAdapter = false)
public class GalleryItem {
	@Json(name = "title")
	private String mTitle = "";

	@Json(name = "id")
	private String mId = "";

	@Json(name = "url_s")
	private String mUrl = "";

	@Json(name = "owner")
	private String mOwner = "";

	public GalleryItem(String title, String id, String url, String owner) {
		mTitle = title;
		mId = id;

		if (url != null) {
			mUrl = url;
		}

		if (owner != null) {
			mOwner = owner;
		}
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public String getOwner() { return mOwner; }

	public void setOwner(String owner) { this.mOwner = owner; }

	public Uri getPhotoPageUri() {
		return Uri.parse("https://www.flickr.com/photos/")
				.buildUpon()
				.appendPath(mOwner)
				.appendPath(mId)
				.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GalleryItem that = (GalleryItem) o;
		return mTitle.equals(that.mTitle) && mId.equals(that.mId) && mUrl.equals(that.mUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mTitle, mId, mUrl);
	}

	@Override
	@NonNull
	public String toString() {
		return "GalleryItem{" +
				"mTitle='" + mTitle + '\'' +
				", mId='" + mId + '\'' +
				", mUrl='" + mUrl + '\'' +
				'}';
	}
}
