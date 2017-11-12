package com.github.mproberts.rxdatabindingdemo.search;

import com.github.mproberts.rxdatabindingdemo.data.UserStorage;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;
import com.github.mproberts.rxdatabindingdemo.search.vm.DefaultSearchViewModel;
import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchModule {

    @Provides
    SearchViewModel providesViewModel(UserStorage userStorage, ProfileViewModel.Navigator profileNavigator) {
        return new DefaultSearchViewModel(userStorage, profileNavigator);
    }
}
