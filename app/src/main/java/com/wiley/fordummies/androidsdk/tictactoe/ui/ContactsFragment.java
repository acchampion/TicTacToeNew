package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import timber.log.Timber;

/**
 * Fragment for displaying contacts.
 * <p>
 * Source: https://github.com/alfongj/android-recyclerview-contacts-example,
 * https://developer.android.com/training/contacts-provider/retrieve-names.html
 * <p>
 * Created by adamcchampion on 2017/08/16.
 */

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private ListView mContactsListView;

	private static final String[] PROJECTION = {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.LOOKUP_KEY,
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
	};

	// An adapter that binds the result Cursor to the ListView
	private SimpleCursorAdapter mCursorAdapter;

	private static final int PERMISSION_REQUEST_READ_CONTACTS = 1;

	// private static final String TAG = ContactsFragment.class.getSimpleName();

	/*
	 * Defines an array that contains column names to move from
	 * the Cursor to the ListView.
	 */
	private final static String[] FROM_COLUMNS = {
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
	};

	/*
	 * Defines an array that contains resource ids for the layout views
	 * that get the Cursor column contents. The id is pre-defined in
	 * the Android framework, so it is prefaced with "android.R.id"
	 */
	private final static int[] TO_IDS = {
			R.id.contact_info
	};

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Activity activity = requireActivity();
		mContactsListView = activity.findViewById(R.id.contact_list_view);
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
			Timber.e("Could not set subtitle");
		}
	}


	private void requestContacts() {
		Timber.d("requestContacts()");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (lacksReadContactPermission()) {
				requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
						PERMISSION_REQUEST_READ_CONTACTS);
			} else {
				showContacts();
			}
		} else {
			showContacts();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	private boolean lacksReadContactPermission() {
		Activity activity = requireActivity();
		return activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				showContacts();
			} else {
				Timber.e("Error: Permission denied to read contacts");

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (lacksReadContactPermission()) {
						AppCompatActivity activity = (AppCompatActivity) requireActivity();
						FragmentManager fm = activity.getSupportFragmentManager();
						ContactPermissionDeniedDialogFragment dialogFragment = new ContactPermissionDeniedDialogFragment();
						dialogFragment.show(fm, "contact_perm_denied");
					}
				}
			}
		}
	}

	private void showContacts() {
		Timber.d("showContacts()");
		Activity activity = requireActivity();

		// Gets a CursorAdapter
		mCursorAdapter = new SimpleCursorAdapter(
				activity,
				R.layout.list_item_contact,
				null,
				FROM_COLUMNS,
				TO_IDS,
				0);
		// Sets the adapter for the ListView
		mContactsListView.setAdapter(mCursorAdapter);

		// Initializes the loader
		LoaderManager.getInstance(this).initLoader(0, null, this);
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(
				requireActivity(),
				ContactsContract.Contacts.CONTENT_URI,
				PROJECTION,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
		);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
		// Put the result Cursor in the adapter for the ListView
		mCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		mCursorAdapter.swapCursor(null);
	}
}
