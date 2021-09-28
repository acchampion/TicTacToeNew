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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.StringUtils;
import com.wiley.fordummies.androidsdk.tictactoe.model.UserAccount;
import com.wiley.fordummies.androidsdk.tictactoe.model.UserAccountViewModel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

/**
 * Fragment for login screen.
 *
 * Created by adamcchampion on 2017/08/03. Modified on 2020/08/06 by acc.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private UserAccountViewModel mUserAccountViewModel;

    private final String TAG = getClass().getSimpleName();
    private final static String OPT_NAME = "name";

    @Override
	public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);

    	Activity activity = requireActivity();
    	mUserAccountViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(UserAccountViewModel.class);
    	// Here's a dummy observer object that indicates when the UserAccounts change in the database.
		mUserAccountViewModel.getAllUserAccounts().observe((LifecycleOwner) activity, userAccounts ->
				Timber.tag(TAG).d("The list of UserAccounts just changed; it has %s elements", userAccounts.size()));
	}

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

        final Button loginButton = v.findViewById(R.id.login_button);
        if (loginButton != null) {
            loginButton.setOnClickListener(this);
        }
        final Button cancelButton = v.findViewById(R.id.cancel_button);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(this);
        }
        final Button newUserButton = v.findViewById(R.id.new_user_button);
        if (newUserButton != null) {
            newUserButton.setOnClickListener(this);
        }

        return v;
    }

    private void checkLogin() {
        final String username = mUsernameEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] sha256HashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			String sha256HashStr = StringUtils.bytesToHex(sha256HashBytes);

			Activity activity = requireActivity();

			UserAccount userAccount = new UserAccount(username, sha256HashStr);
			LiveData<List<UserAccount>> userAccountListData = mUserAccountViewModel.getAllUserAccounts();
			List<UserAccount> userAccountList = userAccountListData.getValue();



			if (Objects.requireNonNull(userAccountList).contains(userAccount)) {
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
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
        	Timber.tag(TAG).e("Invalid button click!");
		}
	}
}
