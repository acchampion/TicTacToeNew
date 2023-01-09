package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;


/**
 * Single point of accessing UserAccount data in the app.
 *
 * Source: <a href="https://developer.android.com/codelabs/android-room-with-a-view">...</a>
 *
 * Created by acc on 2021/08/04.
 */
public class UserAccountRepository {

	private final UserAccountDao mUserAccountDao;

	private final String TAG = getClass().getSimpleName();

	public UserAccountRepository(Application application) {
		UserAccountDatabase db = UserAccountDatabase.getDatabase(application);
		mUserAccountDao = db.getUserAccountDao();
	}

	// Room executes all queries on a separate thread.
	// Observed LiveData notify the observer upon data change.
	public LiveData<List<UserAccount>> getAllUserAccounts() {
		return mUserAccountDao.getAllUserAccounts();
	}

	public LiveData<UserAccount> findUserAccountByName(UserAccount account) {
		return mUserAccountDao.findByName(account.getName(), account.getPassword());
	}

	// You MUST call this on a non-UI thread or the app will throw an exception.
	// I'm passing a Runnable object to the database.
	public void insert(UserAccount account) {
		UserAccountDatabase.databaseWriteExecutor.execute(() ->
				mUserAccountDao.insert(account));
	}

	// Similarly, I'm calling update() on a non-UI thread.
	public void update(UserAccount... accounts) {
		UserAccountDatabase.databaseWriteExecutor.execute(() ->
				mUserAccountDao.update(accounts));
	}

	// Similarly, I'm calling delete() on a non-UI thread.
	public void delete(UserAccount... accounts) {
		UserAccountDatabase.databaseWriteExecutor.execute(() ->
				mUserAccountDao.delete(accounts));
	}
}
