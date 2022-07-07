package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.wiley.fordummies.androidsdk.tictactoe.R;
import com.wiley.fordummies.androidsdk.tictactoe.ui.activity.HelpWebViewActivity;

import timber.log.Timber;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpFragment extends Fragment implements View.OnClickListener {

    private final String mUrlStr = "https://en.wikipedia.org/wiki/Tic-tac-toe";
    private static final String TAG = HelpFragment.class.getSimpleName();

	@Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);

        Button wikipedia = v.findViewById(R.id.button_wikipedia);
        wikipedia.setOnClickListener(this);
        Button wikipediaWebView = v.findViewById(R.id.button_wikipedia_webview);
        wikipediaWebView.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppCompatActivity activity = (AppCompatActivity) requireActivity();

			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setSubtitle(getResources().getString(R.string.help));
			}
		} catch (NullPointerException npe) {
            Timber.tag(TAG).e("Could not set subtitle");
        }
    }

	private boolean hasNetworkConnection() {
		Context ctx = requireContext();
		ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (connMgr.isDefaultNetworkActive() && networkInfo != null && networkInfo.isConnected());
    }

    private void launchBrowser() {
        Uri theUri = Uri.parse(mUrlStr);
        Intent launchBrowserIntent = new Intent(Intent.ACTION_VIEW, theUri);
        startActivity(launchBrowserIntent);
    }

    private void launchWebView() {
		Activity activity = requireActivity();
		Intent launchWebViewIntent = new Intent(activity.getApplicationContext(), HelpWebViewActivity.class);
		launchWebViewIntent.putExtra("url", mUrlStr);
		startActivity(launchWebViewIntent);
	}

    private void notifyNoNetworkConnection() {
        FragmentManager manager = getParentFragmentManager();
        NoNetworkConnectionDialogFragment fragment = new NoNetworkConnectionDialogFragment();
		fragment.show(manager, "no_net_conn");
	}

    @Override
    public void onClick(View view) {
		final int viewId = view.getId();
		if (viewId == R.id.button_wikipedia) {
			if (hasNetworkConnection()) {
				launchBrowser();
			} else {
				notifyNoNetworkConnection();
			}
		} else if (viewId == R.id.button_wikipedia_webview) {
			if (hasNetworkConnection()) {
				launchWebView();
			} else {
				notifyNoNetworkConnection();
			}
		} else {
			Timber.tag(TAG).e("Invalid button click!");
		}
    }
}
