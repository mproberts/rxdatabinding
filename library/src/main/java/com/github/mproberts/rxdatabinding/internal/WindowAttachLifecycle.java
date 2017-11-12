package com.github.mproberts.rxdatabinding.internal;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;

public class WindowAttachLifecycle extends Lifecycle implements OnAttachStateChangeListener {
    private final View _view;

    public WindowAttachLifecycle(View view) {
        _view = view;
    }

    @Override
    protected void attach() {
        _view.addOnAttachStateChangeListener(this);

        if (_view.getWindowToken() != null) {
            setActive(true);
        }
    }

    @Override
    protected void detach() {
        _view.removeOnAttachStateChangeListener(this);
    }

    @Override
    public void onViewAttachedToWindow(View view) {
        setActive(true);
    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        setActive(false);
    }
}
