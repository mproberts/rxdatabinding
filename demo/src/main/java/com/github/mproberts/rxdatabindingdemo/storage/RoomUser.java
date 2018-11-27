package com.github.mproberts.rxdatabindingdemo.storage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.github.mproberts.rxdatabindingdemo.data.User;
import com.github.mproberts.rxdatabindingdemo.data.UserId;

@Entity(tableName = "users")
public class RoomUser implements User {

    public RoomUser(UserId id, String displayName, String photoUrl, String username, boolean isPremium) {
        this.id = id;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.username = username;
        this.isPremium = isPremium;
    }

    public RoomUser(User other) {
        this(other.id(), other.displayName(), other.photoUrl(), other.username(), other.isPremium());
    }

    @Override
    @NonNull
    public UserId id() {
        return id;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public boolean isPremium() {
        return isPremium;
    }

    @Override
    public String photoUrl() {
        return photoUrl;
    }

    public static class TypeConverters {

        @androidx.room.TypeConverter
        public static UserId convertToUserId(String id) {
            return new UserId(id);
        }

        @androidx.room.TypeConverter
        public static String convertFromUserId(UserId id) {
            return id.value;
        }
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    public final UserId id;

    @ColumnInfo(name = "display_name")
    public final String displayName;

    @ColumnInfo(name = "photo_url")
    public final String photoUrl;

    @ColumnInfo(name = "username")
    public final String username;

    @ColumnInfo(name = "is_premium")
    public final boolean isPremium;
}
