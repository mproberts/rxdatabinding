package com.github.mproberts.rxdatabindingdemo.profile.vm;

import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;

public interface ProfileViewModel {
    interface Navigator {
        void navigateToProfile(UserId userId);
    }

    Flowable<Optional<String>> displayName();

    Flowable<Optional<String>> username();

    Flowable<Optional<String>> profilePhoto();

    Flowable<Boolean> isPremium();
}
