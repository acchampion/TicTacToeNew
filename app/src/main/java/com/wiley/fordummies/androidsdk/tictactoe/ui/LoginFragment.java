package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.model.Account;
import com.wiley.fordummies.androidsdk.tictactoe.model.AccountDbHelper;
import com.wiley.fordummies.androidsdk.tictactoe.model.AccountSingleton;

import java.util.List;

import timber.log.Timber;

/**
 * Fragment for login screen.
 *
 * Created by adamcchampion on 2017/08/03.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private AccountSingleton mAccountSingleton;
    private AccountDbHelper mDbHelper;

    private final static String OPT_NAME = "name";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        Activity activity = requireActivity();

		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			v = inflater.inflate(R.layout.fragment_login_land, container, false);
		} else {
			v = inflater.inflate(R.layout.fragment_login, container, false);
		}

		mUsernameEditText = v.findViewById(R.id.username_text);
        mPasswordEditText = v.findViewById(R.id.password_text);

        Button loginButton = v.findViewById(R.id.login_button);
        if (loginButton != null) {
            loginButton.setOnClickListener(this);
        }
        Button cancelButton = v.findViewById(R.id.cancel_button);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(this);
        }
        Button newUserButton = v.findViewById(R.id.new_user_button);
        if (newUserButton != null) {
            newUserButton.setOnClickListener(this);
        }

        return v;
    }

    private void checkLogin() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        Activity activity = requireActivity();

        if (mAccountSingleton == null) {
			mAccountSingleton = AccountSingleton.get(activity.getApplicationContext());
		}

        if (mDbHelper == null) {
			mDbHelper = new AccountDbHelper(activity.getApplicationContext());
		}

		List<Account> accountList = mAccountSingleton.getAccounts();
		boolean hasMatchingAccount = false;
		for (Account account : accountList) {
			if (account.getName().equals(username) && account.getPassword().equals(password)) {
				hasMatchingAccount = true;
				break;
			}
		}

		if (accountList.size() > 0 && hasMatchingAccount) {
			// Save username as the name of the player
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(OPT_NAME, username);
			editor.apply();

			// Bring up the GameOptions screen
			startActivity(new Intent(activity, GameOptionsActivity.class));
			activity.finish();
		} else {
			FragmentManager manager = getParentFragmentManager();
			LoginErrorDialogFragment fragment = new LoginErrorDialogFragment();
			fragment.show(manager, "login_error");
		}
	}

    @Override
    public void onClick(View view) {
        final Activity activity = requireActivity();
        final int viewId = view.getId();

        if (viewId == R.id.login_button) {
			checkLogin();
		} else if (viewId == R.id.cancel_button) {
			activity.finish();
		} else if (viewId == R.id.new_user_button) {
			final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
			FragmentManager fm = getParentFragmentManager();
			Fragment fragment = new AccountFragment();
			if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
				fm.beginTransaction()
						.replace(R.id.fragment_container, fragment)
						.addToBackStack("account_fragment")
						.commit();
			}
		} else {
        	Timber.e("Invalid button click!");
		}
	}
}
