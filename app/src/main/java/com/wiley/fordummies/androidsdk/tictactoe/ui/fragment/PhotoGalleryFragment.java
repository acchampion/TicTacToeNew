package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.VisibleFragment;
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;
import com.wiley.fordummies.androidsdk.tictactoe.model.QueryPreferences;
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.PhotoGalleryViewModel;
import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker;
import com.wiley.fordummies.androidsdk.tictactoe.network.ThumbnailDownloader;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class PhotoGalleryFragment extends VisibleFragment {

	private PhotoGalleryViewModel mPhotoGalleryViewModel;
	private RecyclerView mPhotoRecyclerView;
	private Lifecycle mLifecycle;
	private final Handler mResponseHandler = new Handler(Looper.getMainLooper());
	private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

	private final String TAG = getClass().getSimpleName();
	private final String POLL_WORK = "POLL_WORK";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
		setHasOptionsMenu(true);

		Activity activity = requireActivity();
		mPhotoGalleryViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(PhotoGalleryViewModel.class);

		mThumbnailDownloader = new ThumbnailDownloader<>(mResponseHandler);
		mThumbnailDownloader.setPriority(Process.THREAD_PRIORITY_DISPLAY);
		mThumbnailDownloader.setThumbnailDownloadListener((holder, bitmap) -> {
			Drawable drawable = new BitmapDrawable(getResources(), bitmap);
			holder.bindDrawable(drawable);
		});
		mThumbnailDownloader.start();
		mThumbnailDownloader.getLooper();

		mLifecycle = getLifecycle();
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
	public void onResume() {
		super.onResume();
		Timber.tag(TAG).d("onResume()");
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();

			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.photo_gallery));
			}
		} catch (NullPointerException npe) {
			Timber.tag(TAG).e("Could not set subtitle");
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPhotoGalleryViewModel.getGalleryItemLiveData().observe(getViewLifecycleOwner(),
				galleryItems -> {
					PhotoAdapter adapter = new PhotoAdapter(galleryItems);
					adapter.notifyDataSetChanged();
					mPhotoRecyclerView.setAdapter(adapter);
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

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_photo_gallery, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_item_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Timber.tag(TAG).d("QueryTextSubmit: %s", query);
				mPhotoGalleryViewModel.fetchPhotos(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Timber.tag(TAG).d("QueryTextChange: %s", newText);
				return false;
			}
		});

		searchView.setOnSearchClickListener(v ->
				searchView.setQuery(mPhotoGalleryViewModel.getSearchTerm(), false));

		MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
		boolean isPolling = QueryPreferences.isPolling(requireContext());
		int toggleItemTitle;

		if (isPolling) {
			toggleItemTitle = R.string.stop_polling;
		} else {
			toggleItemTitle = R.string.start_polling;
		}
		toggleItem.setTitle(toggleItemTitle);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		final int menuItemId = item.getItemId();
		if (menuItemId == R.id.menu_item_clear) {
			mPhotoGalleryViewModel.fetchPhotos();
			return true;
		} else if (menuItemId == R.id.menu_item_toggle_polling) {
			boolean isPolling = QueryPreferences.isPolling(requireContext());
			if (isPolling) {
				WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK);
				QueryPreferences.setPolling(requireContext(), false);
			} else {
				Constraints constraints = new Constraints.Builder()
						.setRequiredNetworkType(NetworkType.UNMETERED)
						.build();
				PeriodicWorkRequest periodicRequest =
						new PeriodicWorkRequest.Builder(PollWorker.class, 15, TimeUnit.MINUTES)
								.setConstraints(constraints)
								.build();
				WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(POLL_WORK,
						ExistingPeriodicWorkPolicy.KEEP,
						periodicRequest);
				QueryPreferences.setPolling(requireContext(), true);
			}
			requireActivity().invalidateOptionsMenu();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private final ImageView mItemImageView;
		private GalleryItem mGalleryItem;

		public PhotoHolder(LayoutInflater inflater, ViewGroup parent) {
			super(inflater.inflate(R.layout.list_item_gallery, parent, false));
			mItemImageView = itemView.findViewById(R.id.photo_info);
			itemView.setOnClickListener(this);
		}

		public void bindDrawable(Drawable drawable) {
			mItemImageView.setImageDrawable(drawable);
		}

		public void bindGalleryItem(GalleryItem item) {
			mGalleryItem = item;
		}

		@Override
		public void onClick(View v) {
			Context context = requireContext();
			CustomTabsIntent intent = new CustomTabsIntent.Builder()
					.setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
					.setShowTitle(true)
					.build();
			intent.launchUrl(context, mGalleryItem.getPhotoPageUri());
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
			holder.bindGalleryItem(galleryItem);
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
