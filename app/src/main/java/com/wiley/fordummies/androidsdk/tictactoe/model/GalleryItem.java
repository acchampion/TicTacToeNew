package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class GalleryItem {
	@SerializedName("title")
	private String mTitle = "";

	@SerializedName("id")
	private String mId = "";

	@SerializedName("url_s")
	private String mUrl = "";

	@SerializedName("owner")
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
		return mTitle.equals(that.mTitle) && mId.equals(that.mId) && Objects.equals(mUrl, that.mUrl);
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
