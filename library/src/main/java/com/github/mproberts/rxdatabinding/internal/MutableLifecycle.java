package com.github.mproberts.rxdatabinding.internal;

abstract class MutableLifecycle extends Lifecycle {

    @Override
    public void setActive(boolean isActive) {
        super.setActive(isActive);
    }
}
