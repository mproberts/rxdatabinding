package com.github.mproberts.rxdatabindingdemo.storage;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.github.mproberts.rxdatabindingdemo.data.UserStorage;

import io.reactivex.Scheduler;

@Database(entities = {RoomUser.class}, version = 1)
@TypeConverters(value = {RoomUser.TypeConverters.class})
public abstract class RoomStorage extends RoomDatabase {

    private static RoomStorage INSTANCE;

    public abstract UserDao userDao();

    private UserStorage _users;

    public UserStorage users() {
        return _users;
    }

    public void setup(Scheduler storageScheduler) {
        _users = new RoomUserStorage(userDao(), storageScheduler);
    }

    public static RoomStorage create(Context appContext, Scheduler storageScheduler) {
        RoomStorage db = Room.databaseBuilder(appContext, RoomStorage.class, "Sample.db")
                .build();

        db.setup(storageScheduler);

        return db;
    }

    public static RoomStorage createInMemory(Context appContext, Scheduler storageScheduler) {
        RoomStorage db = Room.inMemoryDatabaseBuilder(appContext, RoomStorage.class)
                .build();

        db.setup(storageScheduler);

        return db;
    }
}
