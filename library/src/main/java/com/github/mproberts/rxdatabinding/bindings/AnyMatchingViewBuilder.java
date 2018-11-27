package com.github.mproberts.rxdatabinding.bindings;

public abstract class AnyMatchingViewBuilder implements ViewBuilder.MatchingViewBuilder {

    public AnyMatchingViewBuilder() {
    }

    @Override
    public boolean matches(Object model) {
        return true;
    }
}
