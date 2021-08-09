package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;


/**
 * Single point of accessing UserAccount data in the app.
 *
 * Source: https://developer.android.com/codelabs/android-room-with-a-view
 *
 * Created by acc on 2021/08/04.
 */
public class UserAccountRepository {

	private UserAccountDao mUserAccountDao;
	private LiveData<List<UserAccount>> mAllUserAccounts;

	private final String TAG = getClass().getSimpleName();

	UserAccountRepository(Application application) {
		UserAccountDatabase db = UserAccountDatabase.getDatabase(application);
		mUserAccountDao = db.getUserAccountDao();
		mAllUserAccounts = mUserAccountDao.getAllUserAccounts();
	}

	// Room executes all queries on a separate thread.
	// Observed LiveData notify the observer upon data change.
	LiveData<List<UserAccount>> getAllUserAccounts() {
		return mAllUserAccounts;
	}

	LiveData<UserAccount> findUserAccountByName(UserAccount userAccount) {
		LiveData<UserAccount> theUserAccount = mUserAccountDao.findByName(userAccount.getName(), userAccount.getPassword());

//		try {
//			Future<LiveData<UserAccount>> future =
//					(Future<LiveData<UserAccount>>) UserAccountDatabase.databaseWriteExecutor.submit(() -> {
//						mUserAccountDao.findByName(userAccount.getName(), userAccount.getPassword());
//					});
//			while (!future.isDone()) {
//				Timber.d(TAG, "Waiting for query to complete");
//				Thread.sleep(100);
//			}
//			theUserAccount = future.get(2, TimeUnit.SECONDS);
//		} catch (ExecutionException e) {
//			Timber.e(TAG, "Could not find UserAccount by name");
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			Timber.e(TAG, "Database query was interrupted");
//			e.printStackTrace();
//		} catch (TimeoutException e) {
//			Timber.e(TAG, "Query task timed out");
//			e.printStackTrace();
//		}
		return theUserAccount;
	}

	// You MUST call this on a non-UI thread or the app will throw an exception.
	// I'm passing a Runnable object to the database.
	void insert(UserAccount userAccount) {
		UserAccountDatabase.databaseWriteExecutor.execute(() ->
				mUserAccountDao.insert(userAccount));
	}

	// TODO: Implement update() and delete methods()
}
