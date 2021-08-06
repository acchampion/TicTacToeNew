package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;

/**
 * ViewModel for the user account storage.
 */
public class UserAccountViewModel extends AndroidViewModel {

	private UserAccountRepository mRepository;

	private final LiveData<List<UserAccount>> mAllUserAccounts;

	public UserAccountViewModel(@NonNull Application application) {
		super(application);
		mRepository = new UserAccountRepository(application);
		mAllUserAccounts = mRepository.getAllUserAccounts();
	}

	public boolean containsUserAccount(UserAccount userAccount) {
		boolean accountInList = false;

		LiveData<UserAccount> userAccountLiveData = mRepository.findUserAccountByName(userAccount);
		UserAccount theUserAccount = userAccountLiveData.getValue();
		if (Objects.requireNonNull(theUserAccount).getName().equals(userAccount.getName()) &&
				Objects.requireNonNull(theUserAccount).getPassword().equals(userAccount.getPassword())) {
			accountInList = true;
		}

		return accountInList;
	}

	public LiveData<UserAccount> getUserAccount(UserAccount userAccount) {
		return mRepository.findUserAccountByName(userAccount);
	}

	public LiveData<List<UserAccount>> getAllUserAccounts() { return mAllUserAccounts; }

	public void insert(UserAccount userAccount) {
		mRepository.insert(userAccount);
	}
}
