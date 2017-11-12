package com.github.mproberts.rxdatabindingdemo.home;

import com.github.mproberts.rxdatabindingdemo.home.vm.HomeViewModel;

import dagger.Subcomponent;

@Subcomponent(modules = {HomeModule.class})
public interface HomeComponent {

    HomeViewModel viewModel();
}
