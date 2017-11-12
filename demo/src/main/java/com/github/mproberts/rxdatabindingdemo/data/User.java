package com.github.mproberts.rxdatabindingdemo.data;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public interface User {
    @NonNull
    UserId id();

    String displayName();

    String username();

    boolean isPremium();

    @Nullable
    String photoUrl();

}
