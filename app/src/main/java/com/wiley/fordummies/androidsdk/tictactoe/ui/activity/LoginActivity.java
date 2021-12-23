package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.LoginFragment;

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
