package com.wiley.fordummies.androidsdk.tictactoe;

/**
 * Class for referencing (name, password) table columns in database.
 *
 * Created by adamcchampion on 2017/08/04.
 */

class AccountDbSchema {
    public static final class AccountsTable {
        public static final String NAME = "accounts";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String PASSWORD = "password";
        }
    }
}
