package com.wiley.fordummies.androidsdk.tictactoe.model;

/**
 * Created by adamcchampion on 2017/08/16.
 */
public class Contact {
    private final String mName;

    public Contact(String name) { mName = name; }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        return mName.equals(contact.mName);
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}
