package com.wiley.fordummies.androidsdk.tictactoe.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

/**
 * Created by adamcchampion on 2017/08/16. Updated on 2021/08/09.
 */
@Fts4
@Entity(tableName = "contact")
public class Contact {
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "rowid")
	public int mUid;

	@NonNull
	@ColumnInfo(name = "name")
	public String mName;

    public Contact(@NonNull String name) { mName = name; }

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

	@NonNull
	@Override
	public String toString() {
		return mName;
	}
}
