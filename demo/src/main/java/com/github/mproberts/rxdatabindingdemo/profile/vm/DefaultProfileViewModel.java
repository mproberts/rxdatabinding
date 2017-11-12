package com.github.mproberts.rxdatabindingdemo.profile.vm;

import com.github.mproberts.rxdatabindingdemo.data.User;
import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxdatabindingdemo.data.UserStorage;
import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;

public class DefaultProfileViewModel implements ProfileViewModel {

    private final UserStorage _users;
    private final UserId _userId;

    public DefaultProfileViewModel(UserStorage users, UserId userId) {
        _users = users;
        _userId = userId;
    }

    @Override
    public Flowable<Optional<String>> displayName() {
        return _users
                .userById(_userId)
                .map(User::displayName)
                .map(Optional::ofNullable)
                .startWith(Optional.empty());
    }

    @Override
    public Flowable<Optional<String>> username() {
        return _users
                .userById(_userId)
                .map(User::username)
                .map(Optional::ofNullable)
                .startWith(Optional.empty());
    }

    @Override
    public Flowable<Optional<String>> profilePhoto() {
        return _users
                .userById(_userId)
                .map(User::photoUrl)
                .map(Optional::ofNullable)
                .startWith(Optional.empty());
    }

    @Override
    public Flowable<Boolean> isPremium() {
        return _users
                .userById(_userId)
                .map(User::isPremium)
                .startWith(false);
    }
}
