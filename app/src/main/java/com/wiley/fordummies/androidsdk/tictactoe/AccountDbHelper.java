package com.wiley.fordummies.androidsdk.tictactoe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wiley.fordummies.androidsdk.tictactoe.AccountDbSchema.AccountsTable;

import timber.log.Timber;

/**
 * Account database helper class.
 *
 * Created by adamcchampion on 2017/08/04.
 */
public class AccountDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TicTacToe.db";
    private static final int DATABASE_VERSION = 1;

    // Class name for logging.
    private final String TAG = getClass().getSimpleName();

    AccountDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + AccountsTable.NAME + "(" +
             "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
             AccountsTable.Cols.NAME + " TEXT, " +
             AccountsTable.Cols.PASSWORD + " TEXT" +
            ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Timber.w(TAG, "Upgrading database; dropping and recreating tables.");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccountsTable.NAME);
        onCreate(sqLiteDatabase);
    }
}
