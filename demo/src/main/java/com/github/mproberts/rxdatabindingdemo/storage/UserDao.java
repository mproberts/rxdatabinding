package com.github.mproberts.rxdatabindingdemo.storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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