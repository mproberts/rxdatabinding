package com.github.mproberts.rxdatabindingdemo.home.vm;

import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;

public interface ContactListItemViewModel {

    Flowable<Optional<String>> displayName();

    Flowable<Optional<String>> username();

    Flowable<Optional<String>> profilePhoto();

    Flowable<Boolean> isPremium();

    void onItemTapped();
}
