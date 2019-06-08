package com.wiley.fordummies.androidsdk.tictactoe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.wiley.fordummies.androidsdk.tictactoe.AccountDbSchema.AccountsTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for user Accounts.
 *
 * Created by adamcchampion on 2017/08/04.
 */

public class AccountSingleton {
    private static AccountSingleton sAccount;

    private SQLiteDatabase mDatabase;

    private static final String INSERT_STMT = "INSERT INTO " + AccountsTable.NAME + " (name, password) VALUES (?, ?)" ;

    public static AccountSingleton get(Context context) {
        if (sAccount == null) {
            sAccount = new AccountSingleton(context);
        }
        return sAccount;
    }

    private AccountSingleton(Context context) {
        AccountDbHelper dbHelper = new AccountDbHelper(context.getApplicationContext());
        mDatabase = dbHelper.getWritableDatabase();
    }

    private static ContentValues getContentValues(Account account) {
        ContentValues values = new ContentValues();
        values.put(AccountsTable.Cols.NAME, account.getName());
        values.put(AccountsTable.Cols.PASSWORD, account.getPassword());

        return values;
    }

    /**
     * Add a new user Account to the database. This DB logic uses code from Jake Wharton:
     * http://jakewharton.com/kotlin-is-here/ (slide 61). It's much easier in Kotlin!
     *
     * @param account Account object
     */
    void addAccount(Account account) {
        ContentValues contentValues = getContentValues(account);

        mDatabase.beginTransaction();
        try {
            SQLiteStatement statement = mDatabase.compileStatement(INSERT_STMT);
            statement.bindString(1, contentValues.getAsString(AccountsTable.Cols.NAME));
            statement.bindString(2, contentValues.getAsString(AccountsTable.Cols.PASSWORD));
            statement.executeInsert();
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    /**
     * Delete all user accounts from the database. This DB logic uses code from Jake Wharton:
     * http://jakewharton.com/kotlin-is-here/ (slide 61). It's much easier in Kotlin!
     */
    public void deleteAllAccounts() {
        mDatabase.beginTransaction();
        try {
            mDatabase.delete(AccountsTable.NAME, null, null);
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    private AccountCursorWrapper queryAccounts() {
        Cursor cursor = mDatabase.query(
                AccountsTable.NAME,
                null, // columns; null selects all columns
                null,
                null,
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );

        return new AccountCursorWrapper(cursor);
    }

    List<Account> getAccounts() {
        List<Account> accountList = new ArrayList<>();

        try (AccountCursorWrapper cursor = queryAccounts()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                accountList.add(cursor.getAccount());
                cursor.moveToNext();
            }
        }

        return accountList;
    }
}
