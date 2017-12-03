package com.github.mproberts.rxdatabindingdemo.profile.activity;

import com.github.mproberts.rxdatabindingdemo.R;
import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxdatabindingdemo.di.AndroidBindingProfileActivity;
import com.github.mproberts.rxdatabindingdemo.profile.ProfileModule;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;

public class ProfileActivity extends AndroidBindingProfileActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_profile;
    }

    @Override
    public ProfileViewModel navigateToProfile(UserId userId) {
        return getDomainComponent()
                .plus(new ProfileModule(userId))
                .viewModel();
    }
}
