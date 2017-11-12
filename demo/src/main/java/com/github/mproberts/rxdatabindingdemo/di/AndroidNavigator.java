package com.github.mproberts.rxdatabindingdemo.di;

import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxdatabindingdemo.profile.activity.ProfileActivity;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;
import com.github.mproberts.rxdatabindingdemo.search.activity.SearchActivity;
import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;

public interface AndroidNavigator extends ProfileViewModel.Navigator, SearchViewModel.Navigator {

    @Override
    @ActivityNavigation(ProfileActivity.class)
    void navigateToProfile(UserId userId);

    @Override
    @ActivityNavigation(SearchActivity.class)
    void navigateToSearch();
}
