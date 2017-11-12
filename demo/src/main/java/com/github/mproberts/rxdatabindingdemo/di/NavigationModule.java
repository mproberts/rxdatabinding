package com.github.mproberts.rxdatabindingdemo.di;

import android.content.Context;

import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;
import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class NavigationModule {

    @Provides
    public ProfileViewModel.Navigator providesProfileNavigator(Context context) {
        return Navigation.create(context, AndroidNavigator.class);
    }

    @Provides
    public SearchViewModel.Navigator providesSearchNavigator(Context context) {
        return Navigation.create(context, AndroidNavigator.class);
    }
}
