package com.github.mproberts.rxdatabinding.bindings;

import androidx.databinding.BindingAdapter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.widget.TextView;

import com.github.mproberts.rxdatabinding.tools.DataBindingTools;
import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class TextViewDataBindings {
    private TextViewDataBindings() {
    }

    @BindingAdapter(value = {"android:textColor", "colorLevel"})
    public static void bindAndroidLevelTextColorBoolean(final TextView view, Drawable drawable, Flowable<Boolean> newValue) {
        bindAndroidLevelTextColor(view, drawable, newValue.map(new Function<Boolean, Integer>() {
            @Override
            public Integer apply(@NonNull Boolean aBoolean) throws Exception {
                return aBoolean ? 1 : 0;
            }
        }));
    }

    @BindingAdapter(value = {"android:textColor", "colorLevel"})
    public static void bindAndroidLevelTextColor(final TextView view, Drawable drawable, Flowable<Integer> newValue) {
        if (!(drawable instanceof LevelListDrawable)) {
            return;
        }

        final LevelListDrawable levelListDrawable = (LevelListDrawable) drawable;

        DataBindingTools.bindViewProperty(android.R.attr.background, new Consumer<Integer>() {
            @Override
            public void accept(Integer level) throws Exception {
                levelListDrawable.setLevel(level);

                Drawable current = levelListDrawable.getCurrent();

                if (current instanceof ColorDrawable) {
                    ColorDrawable colorCurrent = (ColorDrawable) current;

                    view.setTextColor(colorCurrent.getColor());
                }
            }
        }, view, newValue);
    }

    @BindingAdapter("android:maxLines")
    public static void bindAndroidMaxLines(final TextView view, Flowable<Integer> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.maxLines, new Consumer<Integer>() {
            @Override
            public void accept(Integer lines) throws Exception {
                view.setMaxLines(lines);
            }
        }, view, newValue);
    }

    @BindingAdapter("android:text")
    public static void bindAndroidTextOptional(final TextView view, Flowable<Optional<String>> newValue) {
        bindAndroidTextOptional(view, newValue, null);
    }

    @BindingAdapter(value = {"android:text", "defaultText"})
    public static void bindAndroidTextOptional(final TextView view, Flowable<Optional<String>> newValue, final String defaultText) {
        DataBindingTools.bindViewProperty(android.R.attr.text, new Consumer<Optional<String>>() {
            @Override
            public void accept(Optional<String> s) throws Exception {
                view.setText(s.orElse(defaultText));
            }
        }, view, newValue.startWith(Optional.ofNullable(defaultText)));
    }

    @BindingAdapter("android:text")
    public static void bindAndroidText(final TextView view, Flowable<String> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.text, new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                view.setText(s);
            }
        }, view, newValue);
    }
}
