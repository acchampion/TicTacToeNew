package com.wiley.fordummies.androidsdk.tictactoe.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class GalleryItem {
	@SerializedName("title")
	private String mTitle = "";

	@SerializedName("id")
	private String mId = "";

	@SerializedName("url_s")
	private String mUrl = "";

	public GalleryItem(String title, String id, String url) {
		mTitle = title;
		mId = id;

		if (url != null) {
			mUrl = null;
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
	public String toString() {
		return "GalleryItem{" +
				"mTitle='" + mTitle + '\'' +
				", mId='" + mId + '\'' +
				", mUrl='" + mUrl + '\'' +
				'}';
	}
}
