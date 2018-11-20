package com.github.mproberts.rxdatabinding.bindings;

import android.content.Context;
import android.view.View;

import io.reactivex.disposables.CompositeDisposable;

public abstract class SimpleViewBuilder<T, TView extends View> implements ViewBuilder<T, TView> {

    @Override
    public int findType(T model) {
        return 0;
    }

    @Override
    public void bind(Context context, TView view, T model, int layoutType, CompositeDisposable lifecycle) {
    }

    @Override
    public boolean recycle(TView view, int layoutType) {
        return true;
    }
}
