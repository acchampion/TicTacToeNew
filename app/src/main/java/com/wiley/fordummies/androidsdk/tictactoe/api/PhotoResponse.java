package com.wiley.fordummies.androidsdk.tictactoe.api;

import androidx.annotation.NonNull;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonClass(generateAdapter = false)
public class PhotoResponse {
	@Json(name = "photo")
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

	@Override @NonNull
	public String toString() {
		return "PhotoResponse{" +
				"mGalleryItems=" + mGalleryItems +
				'}';
	}
}
