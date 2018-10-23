package com.github.mproberts.rxdatabinding.bindings;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.github.mproberts.rxdatabinding.tools.DataBindingTools;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public final class ImageViewDataBindings {
    private ImageViewDataBindings() {
    }

    @BindingAdapter("android:imageLevel")
    public static void bindAndroidLevelListInteger(final ImageView view, Flowable<Integer> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.drawable, new Consumer<Integer>() {
            @Override
            public void accept(Integer level) throws Exception {
                view.setImageLevel(level);
            }
        }, view, newValue);
    }

    @BindingAdapter("android:imageLevel")
    public static void bindAndroidLevelListBoolean(final ImageView view, Flowable<Boolean> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.drawable, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) throws Exception {
                view.setImageLevel(enabled ? 1 : 0);
            }
        }, view, newValue);
    }

    @BindingAdapter(value = {"android:imageLevel", "adapter"})
    public static void bindAndroidLevelListAdapter(final ImageView view, Flowable<Object> newValue, final LevelValueAdapter adapter) {
        DataBindingTools.bindViewProperty(android.R.attr.drawable, new Consumer<Object>() {
            @Override
            public void accept(Object object) throws Exception {
                view.setImageLevel(adapter.convertValue(object));
            }
        }, view, newValue);
    }

    @BindingAdapter(value = {"data", "ifTrue", "ifFalse"})
    public static void bindAndroidDrawableBoolean(final ImageView view, Flowable<Boolean> newValue, final Drawable a, final Drawable b) {
        DataBindingTools.bindViewProperty(android.R.attr.drawable, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) throws Exception {
                view.setImageDrawable(enabled ? a : b);
            }
        }, view, newValue);
    }
}
