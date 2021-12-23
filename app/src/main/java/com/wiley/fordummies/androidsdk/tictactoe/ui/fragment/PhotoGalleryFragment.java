package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.PhotoGalleryViewModel;
import com.wiley.fordummies.androidsdk.tictactoe.model.ThumbnailDownloader;

import java.util.List;
import java.util.Objects;

public class PhotoGalleryFragment extends Fragment {

	private PhotoGalleryViewModel mPhotoGalleryViewModel;
	private RecyclerView mPhotoRecyclerView;
	private Lifecycle mLifecycle;
	private Handler mResponseHandler;
	private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		Activity activity = requireActivity();
		mPhotoGalleryViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(PhotoGalleryViewModel.class);

		mLifecycle = getLifecycle();

		mResponseHandler = new Handler();
		mThumbnailDownloader = new ThumbnailDownloader<>(mResponseHandler);
		mThumbnailDownloader.setThumbnailDownloadListener((holder, bitmap) -> {
			Drawable drawable = new BitmapDrawable(getResources(), bitmap);
			holder.bindDrawable(drawable);
		});
		mThumbnailDownloader.start();
		mThumbnailDownloader.getLooper();

		mLifecycle.addObserver(mThumbnailDownloader.mFragmentLifecycleObserver);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

		mPhotoRecyclerView = view.findViewById(R.id.photo_recycler_view);
		mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
		mLifecycle.addObserver(mThumbnailDownloader.mViewLifecycleObserver);

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPhotoGalleryViewModel.getGalleryItemLiveData().observe(getViewLifecycleOwner(),
				new Observer<List<GalleryItem>>() {
					@Override
					public void onChanged(List<GalleryItem> galleryItems) {
						PhotoAdapter adapter = new PhotoAdapter(galleryItems);
						adapter.notifyDataSetChanged();
						mPhotoRecyclerView.setAdapter(adapter);
					}
				});

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Objects.requireNonNull(mLifecycle).removeObserver(mThumbnailDownloader.mViewLifecycleObserver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLifecycle.removeObserver(mThumbnailDownloader.mFragmentLifecycleObserver);
	}

	private static class PhotoHolder extends RecyclerView.ViewHolder {
		private final ImageView mItemImageView;

		public PhotoHolder(LayoutInflater inflater, ViewGroup parent) {
			super(inflater.inflate(R.layout.list_item_gallery, parent, false));
			mItemImageView = itemView.findViewById(R.id.photo_info);
		}

		public void bindDrawable(Drawable drawable) {
			mItemImageView.setImageDrawable(drawable);
		}
	}

	private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
		private final List<GalleryItem> mGalleryItems;

		public PhotoAdapter(List<GalleryItem> galleryItems) {
			mGalleryItems = galleryItems;
		}

		@NonNull
		@Override
		public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater inflater = requireActivity().getLayoutInflater();
			return new PhotoHolder(inflater, parent);
		}

		@Override
		public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
			GalleryItem galleryItem = mGalleryItems.get(position);
			Drawable placeholder = ContextCompat.getDrawable(requireContext(), R.drawable.image_placeholder);
			holder.bindDrawable(placeholder);
			mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
		}

		@Override
		public int getItemCount() {
			return mGalleryItems.size();
		}
	}
}
