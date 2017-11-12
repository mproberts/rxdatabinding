package com.github.mproberts.rxdatabindingdemo.profile.activity;

import android.content.Intent;

import com.github.mproberts.rxdatabindingdemo.R;
import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxdatabindingdemo.di.DomainComponent;
import com.github.mproberts.rxdatabindingdemo.profile.ProfileModule;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;
import com.github.mproberts.rxdatabindingdemo.tools.DemoActivity;

public class ProfileActivity extends DemoActivity<ProfileViewModel> {
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_profile;
    }

    @Override
    protected ProfileViewModel createViewModel(Intent intent, DomainComponent domainComponent) {
        UserId userId = getArgument(0, UserId.class);

        return domainComponent.plus(new ProfileModule(userId)).viewModel();
    }
}
