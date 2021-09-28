package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;
import com.wiley.fordummies.androidsdk.tictactoe.model.PhotoGalleryViewModel;

import java.util.List;

public class PhotoGalleryFragment extends Fragment {

	private PhotoGalleryViewModel mPhotoGalleryViewModel;
	private RecyclerView mPhotoRecyclerView;
	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Activity activity = requireActivity();
		mPhotoGalleryViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(PhotoGalleryViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

		mPhotoRecyclerView = view.findViewById(R.id.photo_recycler_view);
		mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPhotoGalleryViewModel.getGalleryItemLiveData().observe(getViewLifecycleOwner(),
				new Observer<List<GalleryItem>>() {
					@Override
					public void onChanged(List<GalleryItem> galleryItems) {
						mPhotoRecyclerView.setAdapter(new PhotoAdapter(galleryItems));
					}
				});

	}

	private static class PhotoHolder extends RecyclerView.ViewHolder {
		private final TextView mItemTextView;

		public PhotoHolder(LayoutInflater inflater, ViewGroup parent) {
			super(inflater.inflate(R.layout.list_item_gallery, parent, false));
			mItemTextView = itemView.findViewById(R.id.photo_info);
		}

		public void bindTitle(String title) {
			mItemTextView.setText(title);
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
			holder.bindTitle(galleryItem.getTitle());
		}

		@Override
		public int getItemCount() {
			return mGalleryItems.size();
		}
	}
}
