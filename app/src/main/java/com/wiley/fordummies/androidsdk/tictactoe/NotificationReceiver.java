package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker;

import timber.log.Timber;

public class NotificationReceiver extends BroadcastReceiver {
	private final String TAG = getClass().getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		int resultCode = getResultCode();
		Timber.tag(TAG).i("Received result: %d", resultCode);

		if (resultCode != Activity.RESULT_OK) {
			// A foreground activity canceled the broadcast
			return;
		}

		int requestCode = intent.getIntExtra(PollWorker.REQUEST_CODE, 0);
		Notification notification = intent.getParcelableExtra(PollWorker.NOTIFICATION);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		notificationManager.notify(requestCode, notification);
	}
}
