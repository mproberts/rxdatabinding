package com.github.mproberts.rxdatabindingdemo.home;

import com.github.mproberts.rxdatabindingdemo.data.UserStorage;
import com.github.mproberts.rxdatabindingdemo.home.vm.DefaultHomeViewModel;
import com.github.mproberts.rxdatabindingdemo.home.vm.HomeViewModel;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;
import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

    @Provides
    HomeViewModel providesViewModel(UserStorage userStorage, ProfileViewModel.Navigator profileNavigator, SearchViewModel.Navigator searchNavigator) {
        return new DefaultHomeViewModel(userStorage, profileNavigator, searchNavigator);
    }

}
