package com.wiley.fordummies.androidsdk.tictactoe.network;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.TicTacToeApplication;
import com.wiley.fordummies.androidsdk.tictactoe.api.FlickrResponse;
import com.wiley.fordummies.androidsdk.tictactoe.model.GalleryItem;
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStoreHelper;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStoreSingleton;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.PhotoGalleryActivity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class PollWorker extends Worker {

	private final SettingsDataStoreSingleton mDataStoreSingleton;
	private final SettingsDataStoreHelper mDataStoreHelper;

	private final Context mContext;
	public static final String ACTION_SHOW_NOTIFICATION = "com.wiley.fordummies.androidsdk.tictactoe.SHOW_NOTIFICATION";
	public static final String PERM_PRIVATE = "com.wiley.fordummies.androidsdk.tictactoe.PRIVATE";
	public static final String REQUEST_CODE = "REQUEST_CODE";
	public static final String NOTIFICATION = "NOTIFICATION";

	private final String TAG = getClass().getSimpleName();

	public PollWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
		mContext = context;
		mDataStoreSingleton = SettingsDataStoreSingleton.getInstance(mContext.getApplicationContext());
		RxDataStore<Preferences> mDataStore = mDataStoreSingleton.getDataStore();
		mDataStoreHelper = new SettingsDataStoreHelper(mDataStore);
	}

	@NonNull
	@Override
	public Result doWork() {
		Timber.tag(TAG).i("Work request triggered");
		String query = mDataStoreHelper.getString(Settings.Keys.PREF_SEARCH_QUERY, "");
		String lastResultId = mDataStoreHelper.getString(Settings.Keys.PREF_LAST_RESULT_ID, "");
		FlickrFetchr mFlickrFetchr = new FlickrFetchr();
		List<GalleryItem> itemList = Collections.emptyList();

		try {
			FlickrResponse flickrResponse;
			if (query.isEmpty()) {
				flickrResponse = mFlickrFetchr.fetchPhotosRequest().execute().body();
			} else {
				flickrResponse = mFlickrFetchr.searchPhotosRequest(query).execute().body();
			}
			if (flickrResponse != null) {
				itemList = flickrResponse.getPhotos().getGalleryItems();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (itemList.isEmpty()) {
			return Result.success();
		}

		String resultId = itemList.get(0).getId();
		if (resultId.equals(lastResultId)) {
			Timber.tag(TAG).i("Got an old result: %s", resultId);
		} else {
			Timber.tag(TAG).i("Got a new result: %s", resultId);
			if (mDataStoreHelper.putString(Settings.Keys.PREF_LAST_RESULT_ID, resultId)) {
				Timber.tag(TAG).i("Set last result to %s", resultId);
			} else {
				Timber.tag(TAG).e("Error setting last result to %s", resultId);
			}

			Intent intent = PhotoGalleryActivity.newIntent(mContext);
			PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
					intent, PendingIntent.FLAG_IMMUTABLE);
			Resources resources = mContext.getResources();
			Notification notification = new NotificationCompat.Builder(mContext, TicTacToeApplication.NOTIFICATION_CHANNEL_ID)
					.setTicker(resources.getString(R.string.new_pictures_title))
					.setSmallIcon(android.R.drawable.ic_menu_report_image)
					.setContentTitle(resources.getString(R.string.new_pictures_title))
					.setContentText(resources.getString(R.string.new_pictures_text))
					.setContentIntent(pendingIntent)
					.setAutoCancel(true)
					.build();

			/*NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
			notificationManager.notify(0, notification);

			mContext.sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);*/

			showBackgroundNotification(0, notification);
		}

		return Result.success();
	}

	private void showBackgroundNotification(int requestCode, Notification notification) {
		Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
		intent.putExtra(REQUEST_CODE, requestCode);
		intent.putExtra(NOTIFICATION, notification);
		mContext.sendOrderedBroadcast(intent, PERM_PRIVATE);
	}
}
