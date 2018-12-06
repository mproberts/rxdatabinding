package com.github.mproberts.rxdatabinding.bindings;

public abstract class AnyMatchingViewCreator implements ViewCreator.MatchingViewCreator {

    public AnyMatchingViewCreator() {
    }

    @Override
    public boolean matches(Object model) {
        return true;
    }
}
