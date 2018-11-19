package com.github.mproberts.rxdatabinding.bindings;

public abstract class AnyMatchingViewBinding implements ViewBinding.MatchingViewBinding {

    public AnyMatchingViewBinding() {
    }

    @Override
    public boolean matches(Object model) {
        return true;
    }
}
