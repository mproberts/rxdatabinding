package com.github.mproberts.rxdatabinding.bindings;

public abstract class TypeMatchingViewCreator<T> implements ViewCreator.MatchingViewCreator {

    private final Class<T> _clzz;

    public TypeMatchingViewCreator(Class<T> clzz) {
        _clzz = clzz;
    }

    @Override
    public boolean matches(Object model) {
        return _clzz.isAssignableFrom(model.getClass());
    }
}
