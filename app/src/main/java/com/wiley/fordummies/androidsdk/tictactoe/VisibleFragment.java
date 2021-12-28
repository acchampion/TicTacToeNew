package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker;

import timber.log.Timber;

public class VisibleFragment extends Fragment {

	private final String TAG = getClass().getSimpleName();

	private final BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String toastStr = "Got a broadcast: " + intent.getAction();
			Timber.tag(TAG).i(toastStr);
			// Toast.makeText(requireContext(), toastStr, Toast.LENGTH_LONG).show();
			// If we receive this, we're visible, so cancel the notification
			Timber.tag(TAG).i("Canceling notification");
			setResultCode(Activity.RESULT_CANCELED);
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter(PollWorker.ACTION_SHOW_NOTIFICATION);
		requireActivity().registerReceiver(mOnShowNotification,
				filter,
				PollWorker.PERM_PRIVATE,
				null);
	}

	@Override
	public void onStop() {
		super.onStop();
		requireActivity().unregisterReceiver(mOnShowNotification);
	}
}
