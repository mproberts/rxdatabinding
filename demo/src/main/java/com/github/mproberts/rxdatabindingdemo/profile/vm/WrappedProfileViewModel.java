package com.github.mproberts.rxdatabindingdemo.profile.vm;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.github.mproberts.rxdatabinding.internal.AndroidObservable;

public class WrappedProfileViewModel {

    private final ProfileViewModel _instance;

    public WrappedProfileViewModel(ProfileViewModel instance) {
        _instance = instance;
    }

    public ObservableField<String> displayName() {
        return AndroidObservable.convertOptional(_instance::displayName);
    }

    public ObservableField<String> username() {
        return AndroidObservable.convertOptional(_instance::username);
    }

    public ObservableField<String> profilePhoto() {
        return AndroidObservable.convertOptional(_instance::profilePhoto);
    }

    public ObservableBoolean isPremium() {
        return AndroidObservable.convertBoolean(_instance::isPremium);
    }
}
