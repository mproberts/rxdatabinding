package com.github.mproberts.rxdatabindingdemo.search.vm;

import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;

public interface SearchListItemViewModel {

    Flowable<Optional<String>> displayName();

    Flowable<Optional<String>> username();

    Flowable<Optional<String>> profilePhoto();

    Flowable<Boolean> isPremium();

    void onItemTapped();
}
