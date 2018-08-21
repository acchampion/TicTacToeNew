package com.wiley.fordummies.androidsdk.tictactoe;

import android.support.v4.app.Fragment;

/**
 * Activity for user login.
 *
 * Created by adamcchampion on 2017/08/03.
 */

public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }
}
