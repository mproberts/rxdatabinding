package com.github.mproberts.rxdatabinding.bindings;

public abstract class TypeMatchingViewBuilder<T> implements ViewBuilder.MatchingViewBuilder {

    private final Class<T> _clzz;

    public TypeMatchingViewBuilder(Class<T> clzz) {
        _clzz = clzz;
    }

    @Override
    public boolean matches(Object model) {
        return _clzz.isAssignableFrom(model.getClass());
    }
}
