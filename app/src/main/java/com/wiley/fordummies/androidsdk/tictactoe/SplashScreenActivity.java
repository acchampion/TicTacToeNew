package com.wiley.fordummies.androidsdk.tictactoe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class SplashScreenActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SplashScreenFragment();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }
}
