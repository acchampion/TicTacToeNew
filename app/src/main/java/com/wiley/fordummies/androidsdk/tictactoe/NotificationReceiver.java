package com.wiley.fordummies.androidsdk.tictactoe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.wiley.fordummies.androidsdk.tictactoe.network.PollWorker;

import timber.log.Timber;

public class NotificationReceiver extends BroadcastReceiver {
	private final String TAG = getClass().getSimpleName();

	@SuppressLint("MissingPermission")
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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			if (hasNotificationPermissions(context)) {
				assert notification != null;
				notificationManager.notify(requestCode, notification);
			}
		} else {
            assert notification != null;
            notificationManager.notify(requestCode, notification);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
	private boolean hasNotificationPermissions(Context context) {
		return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
	}
}
