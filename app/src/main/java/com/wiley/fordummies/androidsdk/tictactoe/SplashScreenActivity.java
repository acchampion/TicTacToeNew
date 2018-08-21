package com.wiley.fordummies.androidsdk.tictactoe;

import android.support.v4.app.Fragment;
import android.os.Bundle;

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

    @Override
    protected void onStart() {
        super.onStart();

    }
}
