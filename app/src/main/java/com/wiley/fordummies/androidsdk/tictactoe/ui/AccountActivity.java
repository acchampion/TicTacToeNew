package com.wiley.fordummies.androidsdk.tictactoe.ui;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import androidx.fragment.app.Fragment;

/**
 * Activity for managing user accounts.
 *
 * Created by adamcchampion on 2017/08/05.
 */

public class AccountActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AccountFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }
}
