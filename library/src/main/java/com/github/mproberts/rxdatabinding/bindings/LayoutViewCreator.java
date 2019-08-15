package com.github.mproberts.rxdatabinding.bindings;

import android.content.Context;
import android.os.Build;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.github.mproberts.rxdatabinding.BR;

import io.reactivex.disposables.CompositeDisposable;

public class LayoutViewCreator<T, TView extends View> implements ViewCreator<T, TView> {

    private final int _layoutId;
    private final Choreographer mChoreographer = Choreographer.getInstance();

    public LayoutViewCreator(int layoutId) {
        _layoutId = layoutId;
    }

    @Override
    public int findType(T model) {
        return _layoutId;
    }

    @Override
    public void bind(Context context, final TView view, final T model, int layoutType, CompositeDisposable lifecycle) {
        if (Build.VERSION.SDK_INT < 19) {
            mChoreographer.postFrameCallback(new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long frameTimeNanos) {
                    ViewDataBinding binding = DataBindingUtil.getBinding(view);
                    binding.setVariable(BR.model, model);
                    binding.executePendingBindings();
                }
            });
        } else {
            ViewDataBinding binding = DataBindingUtil.getBinding(view);
            binding.setVariable(BR.model, model);
            binding.executePendingBindings();
        }
    }

    @Override
    public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
        return DataBindingUtil.inflate(inflater, _layoutId, parent, false).getRoot();
    }

    @Override
    public boolean recycle(TView view, int layoutType) {
        return false;
    }
}
