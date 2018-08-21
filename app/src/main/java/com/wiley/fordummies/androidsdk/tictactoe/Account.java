package com.wiley.fordummies.androidsdk.tictactoe;

/**
 * Model-layer class for user account with username and password.
 *
 * Created by adamcchampion on 2017/08/04.
 */

public class Account {
    private String mName;
    private String mPassword;

    Account(String name, String password) {
        mName = name;
        mPassword = password;
    }

    public String getName() {
        return mName;
    }

    public String getPassword() {
        return mPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return mName.equals(account.mName) && mPassword.equals(account.mPassword);
    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mPassword.hashCode();
        return result;
    }
}
