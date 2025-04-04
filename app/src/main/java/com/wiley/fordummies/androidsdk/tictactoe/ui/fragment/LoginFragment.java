package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.StringUtils;
import com.wiley.fordummies.androidsdk.tictactoe.model.Settings;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStoreHelper;
import com.wiley.fordummies.androidsdk.tictactoe.model.SettingsDataStoreSingleton;
import com.wiley.fordummies.androidsdk.tictactoe.model.UserAccount;
import com.wiley.fordummies.androidsdk.tictactoe.model.viewmodel.UserAccountViewModel;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.GameOptionsActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import timber.log.Timber;

/**
 * Fragment for login screen.
 * <p>
 * Created by adamcchampion on 2017/08/03. Modified on 2020/08/06 by acc.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
	private EditText mUsernameEditText;
	private EditText mPasswordEditText;
	private View mHeaderTextView;
	private Button mLoginButton, mCancelButton, mNewUserButton;
	private UserAccountViewModel mUserAccountViewModel;
	private final List<UserAccount> mUserAccountList = new CopyOnWriteArrayList<>();

	private SettingsDataStoreSingleton mDataStoreSingleton;
	private SettingsDataStoreHelper mDataStoreHelper;

	private final String TAG = getClass().getSimpleName();


	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Timber.tag(TAG).d("onCreate()");

		Activity activity = requireActivity();
		mDataStoreSingleton = SettingsDataStoreSingleton.getInstance(requireContext().getApplicationContext());
		mUserAccountViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(UserAccountViewModel.class);
		// Here's a dummy observer object that indicates when the UserAccounts change in the database.

		mUserAccountViewModel.getAllUserAccounts().observe((LifecycleOwner) activity, userAccounts -> {
			Timber.tag(TAG).d("The list of UserAccounts just changed; it has %s elements", userAccounts.size());
			mUserAccountList.clear();
			mUserAccountList.addAll(userAccounts);
		});

		RxDataStore<Preferences> mDataStore = mDataStoreSingleton.getDataStore();
		/* if (mDataStore == null) {
			mDataStore = new RxPreferenceDataStoreBuilder(activity.getApplicationContext(), Settings.NAME).build();
		} */
		mDataStoreHelper = new SettingsDataStoreHelper(mDataStore);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v;
		Timber.tag(TAG).d("onCreateView()");

		v = inflater.inflate(R.layout.fragment_login, container, false);

		mHeaderTextView = v.findViewById(R.id.tictactoe_awaits);
		mUsernameEditText = v.findViewById(R.id.username_text);
		mPasswordEditText = v.findViewById(R.id.password_text);

		mLoginButton = v.findViewById(R.id.login_button);
		mCancelButton = v.findViewById(R.id.cancel_button);
		mNewUserButton =  v.findViewById(R.id.new_user_button);

		Activity activity = requireActivity();
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

		Timber.tag(TAG).d("Registering click listeners");
		if (mLoginButton != null) {
			mLoginButton.setOnClickListener(this);
		}
		if (mCancelButton != null) {
			mCancelButton.setOnClickListener(this);
		}
		if (mNewUserButton != null) {
			if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
				mNewUserButton.setOnClickListener(this);
			} else {
				mNewUserButton.setVisibility(View.GONE);
				mNewUserButton.invalidate();
			}
		}

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		ViewCompat.setOnApplyWindowInsetsListener(mHeaderTextView, new OnApplyWindowInsetsListener() {
			@NonNull
			@Override
			public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
				ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();

				var sysBarsCutoutGestures = insets.getInsets(WindowInsetsCompat.Type.systemBars() |
						WindowInsetsCompat.Type.displayCutout());
				v.setPadding(sysBarsCutoutGestures.left, sysBarsCutoutGestures.top,
						sysBarsCutoutGestures.right, sysBarsCutoutGestures.bottom);
				return WindowInsetsCompat.CONSUMED;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.login));
			}
		} catch (NullPointerException npe) {
			Timber.tag(TAG).e("Could not set subtitle");
		}
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Timber.tag(TAG).d("onDestroyView()");
		Timber.tag(TAG).d("Unregistering click listeners");
		if (mLoginButton != null) {
			mLoginButton.setOnClickListener(null);
		}
		if (mCancelButton != null) {
			mCancelButton.setOnClickListener(null);
		}
		if (mNewUserButton != null) {
			mNewUserButton.setOnClickListener(null);
		}

		mUsernameEditText = null;
		mPasswordEditText = null;
		mLoginButton = mCancelButton = mNewUserButton = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Timber.tag(TAG).d("onDestroy()");
		final Activity activity = requireActivity();
		mUserAccountViewModel.getAllUserAccounts().removeObservers((LifecycleOwner) activity);
		mDataStoreHelper = null;
		mDataStoreSingleton = null;
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

			if (mUserAccountList.contains(userAccount)) {
				String accountName = mDataStoreHelper.getString(Settings.Keys.OPT_NAME, "");
				if (accountName.isEmpty()) {
					// Save username as the name of the player (if it's not there already)
					if (mDataStoreHelper.putString(Settings.Keys.OPT_NAME, username)) {
						Timber.tag(TAG).d("Wrote username successfully to DataStore");
					} else {
						Timber.tag(TAG).e("Error writing username to DataStore");
					}
				}

				// Bring up the GameOptions screen
				startActivity(new Intent(activity, GameOptionsActivity.class));
				activity.finish();
			} else {
				showLoginErrorFragment();
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private void showLoginErrorFragment() {
		FragmentManager manager = getParentFragmentManager();
		LoginErrorDialogFragment fragment = new LoginErrorDialogFragment();
		fragment.show(manager, "login_error");
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
