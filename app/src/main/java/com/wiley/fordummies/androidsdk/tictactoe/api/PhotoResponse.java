package com.wiley.fordummies.androidsdk.tictactoe.api;

import com.google.gson.annotations.SerializedName;
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotoResponse {
	@SerializedName("photo")
	private List<GalleryItem> mGalleryItems;

	public PhotoResponse() {
		mGalleryItems = new ArrayList<>();
	}

	public PhotoResponse(List<GalleryItem> galleryItems) {
		mGalleryItems = galleryItems;
	}

	public List<GalleryItem> getGalleryItems() {
		return mGalleryItems;
	}

	public void setGalleryItems(List<GalleryItem> galleryItems) {
		this.mGalleryItems = galleryItems;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PhotoResponse that = (PhotoResponse) o;
		return mGalleryItems.equals(that.mGalleryItems);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mGalleryItems);
	}

	@Override
	public String toString() {
		return "PhotoResponse{" +
				"mGalleryItems=" + mGalleryItems +
				'}';
	}
}
