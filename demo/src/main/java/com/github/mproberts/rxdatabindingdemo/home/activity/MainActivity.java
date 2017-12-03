package com.github.mproberts.rxdatabindingdemo.home.activity;

import com.github.mproberts.rxdatabindingdemo.R;
import com.github.mproberts.rxdatabindingdemo.di.AndroidBindingMainActivity;
import com.github.mproberts.rxdatabindingdemo.home.HomeModule;
import com.github.mproberts.rxdatabindingdemo.home.vm.HomeViewModel;

public class MainActivity extends AndroidBindingMainActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public HomeViewModel navigateToHome() {
        return getDomainComponent()
                .plus(new HomeModule())
                .viewModel();
    }

    @Override
    protected boolean showBackButton() {
        return false;
    }
}
