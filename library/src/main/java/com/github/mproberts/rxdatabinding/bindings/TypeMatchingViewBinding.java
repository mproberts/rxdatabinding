package com.github.mproberts.rxdatabinding.bindings;

public abstract class TypeMatchingViewBinding<T> implements ViewBinding.MatchingViewBinding {

    private final Class<T> _clzz;

    public TypeMatchingViewBinding(Class<T> clzz) {
        _clzz = clzz;
    }

    @Override
    public boolean matches(Object model) {
        return _clzz.isAssignableFrom(model.getClass());
    }
}
