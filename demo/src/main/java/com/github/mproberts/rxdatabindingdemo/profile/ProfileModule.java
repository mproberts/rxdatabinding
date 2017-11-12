package com.github.mproberts.rxdatabindingdemo.profile;

import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxdatabindingdemo.data.UserStorage;
import com.github.mproberts.rxdatabindingdemo.profile.vm.DefaultProfileViewModel;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileModule {

    private final UserId _userId;

    public ProfileModule(UserId userId) {
        _userId = userId;
    }

    @Provides
    ProfileViewModel providesViewModel(UserStorage userStorage) {
        return new DefaultProfileViewModel(userStorage, _userId);
    }

}
