package com.github.mproberts.rxdatabinding.internal;

import com.github.mproberts.rxdatabinding.tools.DataBindingTools;
import com.github.mproberts.rxdatabinding.tools.UiThreadScheduler;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ViewBinding<T> implements Lifecycle.Listener {
    private final Consumer<T> _binder;
    private final Flowable<T> _observable;
    private final Action _reset;
    private final Lifecycle _lifecycle;
    private final boolean _onMain;

    private Disposable _disposable;

    public ViewBinding(Consumer<T> binder, Flowable<T> observable, Action reset, Lifecycle lifecycle, boolean onMain) {
        _onMain = onMain;
        _binder = binder;
        _observable = observable;
        _reset = reset;
        _lifecycle = lifecycle;

        lifecycle.addListener(this);

        if (lifecycle.isActive()) {
            onActive();
        }
    }

    public void detach() {
        _lifecycle.removeListener(this);

        if (_lifecycle.isActive()) {
            onInactive();
        }

        if (_reset != null) {
            try {
                _reset.run();
            } catch (Exception e) {
                DataBindingTools.handleError(e);
            }
        }
    }

    @Override
    public void onActive() {
        if (_disposable != null) {
            return;
        }

        if (_onMain) {
            _disposable = _observable
                    .observeOn(UiThreadScheduler.uiThread())
                    .subscribe(_binder, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            DataBindingTools.handleError(throwable);
                        }
                    });
        } else {
            _disposable = _observable
                    .subscribeOn(Schedulers.computation())
                    .subscribe(_binder, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            DataBindingTools.handleError(throwable);
                        }
                    });
        }
    }

    @Override
    public void onInactive() {
        Disposable disposable = _disposable;

        _disposable = null;

        if (disposable != null) {
            disposable.dispose();
        }
    }
}
