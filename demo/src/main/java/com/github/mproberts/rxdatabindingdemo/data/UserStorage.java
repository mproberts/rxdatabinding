package com.github.mproberts.rxdatabindingdemo.data;

import com.github.mproberts.rxtools.list.FlowableList;

import io.reactivex.Flowable;

public interface UserStorage {
    FlowableList<Flowable<User>> allContacts();

    FlowableList<Flowable<User>> filteredContacts(String query);

    Flowable<User> userById(UserId userId);
}
