package com.github.mproberts.rxdatabindingdemo.di;

import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxdatabindingdemo.home.activity.MainActivity;
import com.github.mproberts.rxdatabindingdemo.home.vm.HomeViewModel;
import com.github.mproberts.rxdatabindingdemo.profile.activity.ProfileActivity;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;
import com.github.mproberts.rxdatabindingdemo.search.activity.SearchActivity;
import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;
import com.github.mproberts.rxdatabindingdemo.tools.DemoActivity;

import com.github.mproberts.navigator.ActivityNavigation;
import com.github.mproberts.navigator.NavigationSource;

@NavigationSource(baseActivity = DemoActivity.class)
public abstract class Navigator implements ProfileViewModel.Navigator, SearchViewModel.Navigator, HomeViewModel.Navigator {

    @Override
    @ActivityNavigation(ProfileActivity.class)
    public abstract void navigateToProfile(UserId userId);

    @Override
    @ActivityNavigation(MainActivity.class)
    public abstract void navigateToHome();

    @Override
    @ActivityNavigation(SearchActivity.class)
    public abstract void navigateToSearch();
}
