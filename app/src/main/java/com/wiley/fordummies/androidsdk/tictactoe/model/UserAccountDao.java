package com.wiley.fordummies.androidsdk.tictactoe.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserAccountDao {
	@Query("SELECT * FROM useraccount")
	LiveData<List<UserAccount>> getAllUserAccounts();

	@Query("SELECT rowid, name, password FROM useraccount WHERE name LIKE :name AND password LIKE :password LIMIT 1")
	LiveData<UserAccount> findByName(String name, String password);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(UserAccount userAccount);

	@Update
	void update(UserAccount... userAccount);

	@Delete
	void delete(UserAccount... userAccount);
}
