package com.wiley.fordummies.androidsdk.tictactoe;

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
import timber.log.Timber;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class HelpFragment extends Fragment implements View.OnClickListener {

    private final String mUrlStr = "https://en.wikipedia.org/wiki/Tic-tac-toe";
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);

        Button wikipedia = v.findViewById(R.id.button_lookup_wikipedia);
        wikipedia.setOnClickListener(this);
        Button wikipediaWebView = v.findViewById(R.id.button_lookup_wikipedia_in_web_view);
        wikipediaWebView.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppCompatActivity activity = (AppCompatActivity) getActivity();

            if (activity != null) {
                ActionBar actionBar = activity.getSupportActionBar();

                if (actionBar != null) {
                    actionBar.setSubtitle(getResources().getString(R.string.help));
                }
            }
        } catch (NullPointerException npe) {
            Timber.e(TAG, "Could not set subtitle");
        }
    }

    private boolean hasNetworkConnection() {
        Activity activity = getActivity();

        if (activity != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                return (activeNetwork != null && activeNetwork.isConnected());
            }
            return false;
        } else {
            return false;
        }
    }

    private void launchBrowser() {
        Uri theUri = Uri.parse(mUrlStr);
        Intent launchBrowserIntent = new Intent(Intent.ACTION_VIEW, theUri);
        startActivity(launchBrowserIntent);
    }

    private void launchWebView() {
        Activity activity = getActivity();

        if (activity != null) {
            Intent launchWebViewIntent = new Intent(getActivity().getApplicationContext(), HelpWebViewActivity.class);
            launchWebViewIntent.putExtra("url", mUrlStr);
            startActivity(launchWebViewIntent);
        }
    }

    // 0oi1OI!

    private void noNetworkConnectionNotify() {
        FragmentManager manager = getFragmentManager();
        NoNetworkConnectionDialogFragment fragment = new NoNetworkConnectionDialogFragment();

        if (manager != null) {
            fragment.show(manager, "no_net_conn");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_lookup_wikipedia:
                if (hasNetworkConnection()) {
                    launchBrowser();
                } else {
                    noNetworkConnectionNotify();
                }
                break;
            case R.id.button_lookup_wikipedia_in_web_view:
                if (hasNetworkConnection()) {
                    launchWebView();
                } else {
                    noNetworkConnectionNotify();
                }
                break;
        }
    }
}
