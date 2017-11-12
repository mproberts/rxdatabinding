package com.github.mproberts.rxdatabindingdemo.profile;

import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;

import dagger.Subcomponent;

@Subcomponent(modules = {ProfileModule.class})
public interface ProfileComponent {

    ProfileViewModel viewModel();
}
