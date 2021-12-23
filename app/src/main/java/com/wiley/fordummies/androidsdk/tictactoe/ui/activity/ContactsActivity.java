package com.wiley.fordummies.androidsdk.tictactoe.ui.activity;

import androidx.fragment.app.Fragment;

import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.ContactsFragment;

/**
 * Activity that shows users' contacts.
 *
 * Created by adamcchampion on 2017/08/16.
 */

public class ContactsActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ContactsFragment();
    }
}
