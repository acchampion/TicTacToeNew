package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import timber.log.Timber;

/**
 * New Fragment to show contacts in a RecyclerView.
 */
public class ContactsFragment extends Fragment {

	private static final int PERMISSION_REQUEST_READ_CONTACTS = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_contact_list, container, false);
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
	}
}
