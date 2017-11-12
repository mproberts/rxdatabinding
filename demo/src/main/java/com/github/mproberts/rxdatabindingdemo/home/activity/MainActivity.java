package com.github.mproberts.rxdatabindingdemo.home.activity;

import android.content.Intent;

import com.github.mproberts.rxdatabindingdemo.R;
import com.github.mproberts.rxdatabindingdemo.di.DomainComponent;
import com.github.mproberts.rxdatabindingdemo.home.HomeModule;
import com.github.mproberts.rxdatabindingdemo.home.vm.HomeViewModel;
import com.github.mproberts.rxdatabindingdemo.tools.DemoActivity;

public class MainActivity extends DemoActivity<HomeViewModel> {
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected HomeViewModel createViewModel(Intent intent, DomainComponent domainComponent) {
        return domainComponent.plus(new HomeModule()).viewModel();
    }

    @Override
    protected boolean showBackButton() {
        return false;
    }
}
