package com.github.mproberts.rxdatabinding.internal;

import java.util.ArrayList;
import java.util.List;

public abstract class Lifecycle {
    private boolean _isActive;
    private List<Listener> _listeners = new ArrayList<>();

    public interface Listener {
        void onActive();

        void onInactive();
    }

    protected abstract void attach();

    protected abstract void detach();

    public void addListener(Listener listener) {
        _listeners.add(listener);

        if (_listeners.size() == 1) {
            attach();
        }
    }

    public void removeListener(Listener listener) {
        _listeners.remove(listener);

        if (_listeners.size() == 0) {
            detach();
        }
    }

    public boolean isActive() {
        return _isActive;
    }

    protected void setActive(boolean isActive) {
        if (isActive == _isActive) {
            return;
        }

        _isActive = isActive;

        for (int i = 0, l = _listeners.size(); i < l; ++i) {
            Listener listener = _listeners.get(i);

            if (isActive) {
                listener.onActive();
            } else {
                listener.onInactive();
            }
        }
    }
}
