package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.startup.AppInitializer;
import androidx.work.Configuration;

import com.mapbox.maps.loader.MapboxMapsInitializer;

import timber.log.Timber;

public class TicTacToeApplication extends Application implements Configuration.Provider {
	public static final String NOTIFICATION_CHANNEL_ID = "flickr_poll";

	@Override
	public void onCreate() {
		super.onCreate();
		if (BuildConfig.DEBUG) {
			Timber.DebugTree debugTree = new Timber.DebugTree();
			Timber.plant(debugTree);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String name = getString(R.string.notification_channel_name);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}

		AppInitializer.getInstance(this)
				.initializeComponent(MapboxMapsInitializer.class);
	}

	@NonNull
	@Override
	public Configuration getWorkManagerConfiguration() {
		return new Configuration.Builder()
				.setMinimumLoggingLevel(android.util.Log.INFO)
				.build();
	}
}
