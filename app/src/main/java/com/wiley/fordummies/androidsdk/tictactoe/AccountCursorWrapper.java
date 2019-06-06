package com.wiley.fordummies.androidsdk.tictactoe;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.wiley.fordummies.androidsdk.tictactoe.AccountDbSchema.AccountsTable;

/**
 * Created by adamcchampion on 2017/08/04.
 */

class AccountCursorWrapper extends CursorWrapper {
    AccountCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    Account getAccount() {
        String name = getString(getColumnIndex(AccountsTable.Cols.NAME));
        String password = getString(getColumnIndex(AccountsTable.Cols.PASSWORD));

        return new Account(name, password);
    }
}
