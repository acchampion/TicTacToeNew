package com.wiley.fordummies.androidsdk.tictactoe.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wiley.fordummies.androidsdk.tictactoe.model.AccountDbSchema.AccountsTable;

import java.util.Locale;

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
    // private static final String TAG = AccountDbHelper.class.getSimpleName();

    public AccountDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    	final String sqlCreate = "CREATE TABLE " + AccountsTable.NAME +
				"( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
				AccountsTable.Cols.NAME + " TEXT," +
				AccountsTable.Cols.PASSWORD + " TEXT)";
        sqLiteDatabase.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Timber.w("Upgrading database; dropping and recreating tables.");
        final String dropTable = String.format(Locale.US, "DROP TABLE IF EXISTS %s", AccountsTable.NAME);
        sqLiteDatabase.execSQL(dropTable);
        onCreate(sqLiteDatabase);
    }
}
