package com.github.mproberts.rxdatabindingdemo.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.mproberts.rxdatabindingdemo.data.UserId;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users WHERE user_id = :id")
    RoomUser userById(UserId id);

    @Query("SELECT user_id FROM users ORDER BY display_name")
    Flowable<List<UserId>> allUsers();

    @Query("SELECT user_id FROM users WHERE display_name LIKE :query ORDER BY display_name")
    Flowable<List<UserId>> filteredUsers(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateUsers(List<RoomUser> users);
}