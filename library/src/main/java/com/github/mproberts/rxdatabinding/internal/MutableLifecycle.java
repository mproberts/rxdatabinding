package com.github.mproberts.rxdatabinding.internal;

public abstract class MutableLifecycle extends Lifecycle {

    @Override
    public void setActive(boolean isActive) {
        super.setActive(isActive);
    }
}
