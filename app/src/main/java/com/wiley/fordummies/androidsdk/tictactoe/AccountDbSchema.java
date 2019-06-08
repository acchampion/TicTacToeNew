package com.wiley.fordummies.androidsdk.tictactoe;

/**
 * Class for referencing (name, password) table columns in database.
 *
 * Created by adamcchampion on 2017/08/04.
 */

class AccountDbSchema {
    static final class AccountsTable {
        static final String NAME = "accounts";

        static final class Cols {
            static final String NAME = "name";
            static final String PASSWORD = "password";
        }
    }
}
