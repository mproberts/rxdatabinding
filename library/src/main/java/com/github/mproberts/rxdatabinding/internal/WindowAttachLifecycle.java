package com.github.mproberts.rxdatabinding.internal;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;

public class WindowAttachLifecycle extends MutableLifecycle implements OnAttachStateChangeListener {
    private final View _view;
    private boolean _pendingDetach = false;

    public WindowAttachLifecycle(View view) {
        _view = view;
        
        setActive(true);
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
        _pendingDetach = false;
        setActive(true);
    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        _pendingDetach = true;

        view.post(new Runnable() {
            @Override
            public void run() {
                if (_pendingDetach) {
                    _pendingDetach = false;
                    setActive(false);
                }
            }
        });
    }
}
