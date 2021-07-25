package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.StringUtils;
import com.wiley.fordummies.androidsdk.tictactoe.model.Account;
import com.wiley.fordummies.androidsdk.tictactoe.model.AccountSingleton;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

/**
 * Fragment for user account creation.
 *
 * Created by adamcchampion on 2017/08/05.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtConfirm;

    // private static final String TAG = AccountFragment.class.getSimpleName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        Activity activity = requireActivity();
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

		mEtUsername = v.findViewById(R.id.username);
		mEtPassword = v.findViewById(R.id.password);
		mEtConfirm = v.findViewById(R.id.password_confirm);
		Button btnAdd = v.findViewById(R.id.done_button);
		btnAdd.setOnClickListener(this);
		Button btnCancel = v.findViewById(R.id.cancel_button);
		btnCancel.setOnClickListener(this);
		Button btnExit = v.findViewById(R.id.exit_button);
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			btnExit.setOnClickListener(this);
		}
		else {
			btnExit.setVisibility(View.GONE);
			btnExit.invalidate();
		}

		return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.account));
			}
		}
        catch (NullPointerException npe) {
            Timber.e("Could not set subtitle");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
    	final int viewId = view.getId();
    	if (viewId == R.id.done_button) {
			createAccount();
		} else if (viewId == R.id.cancel_button) {
			mEtUsername.setText("");
			mEtPassword.setText("");
			mEtConfirm.setText("");

		} else if (viewId == R.id.exit_button) {
			FragmentActivity activity = requireActivity();
			activity.getSupportFragmentManager().popBackStack();
		} else {
			Timber.e("Invalid button click");
		}
    }

    private void createAccount() {
        FragmentActivity activity = requireActivity();
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        String confirm = mEtConfirm.getText().toString();
		if (password.equals(confirm) && !username.equals("") && !password.equals("")) {
			AccountSingleton singleton = AccountSingleton.get(activity.getApplicationContext());
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] sha256HashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
				String sha256HashStr = StringUtils.bytesToHex(sha256HashBytes);
				Account account = new Account(username, sha256HashStr);
				singleton.addAccount(account);
				Toast.makeText(activity.getApplicationContext(), "New record inserted", Toast.LENGTH_SHORT).show();

			} catch (NoSuchAlgorithmException e) {
				Toast.makeText(activity, "Error: No SHA-256 algorithm found", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		} else if ((username.equals("")) || (password.equals("")) || (confirm.equals(""))) {
			Toast.makeText(activity.getApplicationContext(), "Missing entry", Toast.LENGTH_SHORT).show();
		} else {
			Timber.e("An unknown account creation error occurred.");
			FragmentManager manager = getParentFragmentManager();
			AccountErrorDialogFragment fragment = new AccountErrorDialogFragment();
			fragment.show(manager, "account_error");
		}
	}
}
