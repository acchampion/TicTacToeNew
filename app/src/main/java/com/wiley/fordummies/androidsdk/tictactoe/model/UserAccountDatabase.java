package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Database class for UserAccount processing with Room.
 *
 * Source: <a href="https://developer.android.com/codelabs/android-room-with-a-view">...</a>
 *
 * Created by acc on 2021/08/04.
 */
@Database(entities = {UserAccount.class}, version = 1, exportSchema = false)
public abstract class UserAccountDatabase extends RoomDatabase  {
	public abstract UserAccountDao getUserAccountDao();

	private static volatile UserAccountDatabase sInstance;
	private static final int sNumberOfThreads = 2;
	static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(sNumberOfThreads);

	static UserAccountDatabase getDatabase(final Context context) {
		if (sInstance == null) {
			synchronized (UserAccountDatabase.class) {
				if (sInstance == null) {
					sInstance = Room.databaseBuilder(context.getApplicationContext(),
							UserAccountDatabase.class, "useraccount_database")
							.build();
				}
			}
		}

		return sInstance;
	}
}
