package com.wiley.fordummies.androidsdk.tictactoe;

import android.support.v4.app.Fragment;

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
