package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.model.Contact;
import com.wiley.fordummies.androidsdk.tictactoe.model.ContactLiveData;
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.ContactViewModel;

import java.util.List;

import timber.log.Timber;

/**
 * New Fragment to show contacts in a RecyclerView.
 *
 * Created by acc on 2021/08/09.
 */
public class ContactsFragment extends Fragment {

	private RecyclerView mContactRecyclerView;
	private ContactAdapter mContactAdapter;
	private List<Contact> mContactList;
	private ContactViewModel mContactViewModel;

	private final ActivityResultLauncher<String> mActivityResult = registerForActivityResult(
			new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<>() {
				@Override
				public void onActivityResult(Boolean result) {
					if (result) {
						// We have permission, so show the contacts.
						showContacts();
					} else {
						// The user denied permission to read contacts, so show them a message.
						Timber.tag(TAG).e("Error: Permission denied to read contacts");

						if (lacksReadContactPermission()) {
							AppCompatActivity activity = (AppCompatActivity) requireActivity();
							FragmentManager fm = activity.getSupportFragmentManager();
							ContactPermissionDeniedDialogFragment dialogFragment =
									new ContactPermissionDeniedDialogFragment();
							dialogFragment.show(fm, "contact_perm_denied");
						}
					}
				}
			});

	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Activity activity = requireActivity();
		mContactViewModel = new ContactViewModel(activity.getApplication());
		mContactViewModel.getAllContacts().observe((LifecycleOwner) activity, contactList -> {
			Timber.tag(TAG).d(TAG, "List of contacts changed; %d elements", contactList.size());
			ContactAdapter contactAdapter = new ContactAdapter(contactList);
			mContactRecyclerView.swapAdapter(contactAdapter, true);
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_contact_list, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.contacts));
			}
			requestContacts();
		} catch (NullPointerException npe) {
			Timber.tag(TAG).e("Could not set subtitle");
		}
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Activity activity = requireActivity();
		mContactRecyclerView = view.findViewById(R.id.contact_recycler_view);
		mContactRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
	}

	private void requestContacts() {
		Timber.tag(TAG).d("requestContacts()");
		if (lacksReadContactPermission()) {
			mActivityResult.launch(Manifest.permission.READ_CONTACTS);
		} else {
			showContacts();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	private boolean lacksReadContactPermission() {
		Activity activity = requireActivity();
		return activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED;
	}

	private void showContacts() {
		Timber.tag(TAG).d("showContacts()");

		ContactLiveData allContactsData = mContactViewModel.getAllContacts();
		mContactList = allContactsData.getValue();
		if (mContactList != null) {
			mContactAdapter = new ContactAdapter(mContactList);
			mContactRecyclerView.setAdapter(mContactAdapter);
			mContactRecyclerView.setItemAnimator(new DefaultItemAnimator());
		}
	}

	private static class ContactHolder extends RecyclerView.ViewHolder {
		private final TextView mContactTextView;

		ContactHolder(LayoutInflater inflater, ViewGroup parent) {
			super(inflater.inflate(R.layout.list_item_contact, parent, false));

			mContactTextView = itemView.findViewById(R.id.contact_info);
		}

		void bind(Contact contact) {
			String name = contact.getName();
			mContactTextView.setText(name);
		}
	}

	private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {

		private final List<Contact> mContactList;

		ContactAdapter(List<Contact> contactList) {
			mContactList = contactList;
		}

		@NonNull
		@Override
		public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater inflater = requireActivity().getLayoutInflater();
			return new ContactHolder(inflater, parent);
		}

		@Override
		public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
			Contact contact = mContactList.get(position);
			holder.bind(contact);
		}

		@Override
		public int getItemCount() {
			return mContactList.size();
		}
	}


}
