package com.github.mproberts.rxdatabinding.bindings;

import android.databinding.BindingAdapter;
import android.widget.EditText;

import com.github.mproberts.rxdatabinding.tools.DataBindingTools;
import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public class EditTextViewDataBindings {
    private EditTextViewDataBindings() {
    }

    @BindingAdapter("android:text")
    public static void bindAndroidTextOptional(final EditText view, Flowable<Optional<String>> newValue) {
        bindAndroidTextOptional(view, newValue, null);
    }

    @BindingAdapter(value = {"android:text", "defaultText"})
    public static void bindAndroidTextOptional(final EditText view, Flowable<Optional<String>> newValue, final String defaultText) {
        DataBindingTools.bindViewProperty(android.R.attr.text, new Consumer<Optional<String>>() {
            @Override
            public void accept(Optional<String> s) throws Exception {
                view.setText(s.orElse(defaultText));
            }
        }, view, newValue.startWith(Optional.ofNullable(defaultText)));
    }

    @BindingAdapter("android:text")
    public static void bindAndroidText(final EditText view, Flowable<String> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.text, new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (view.getText() == null || !view.getText().toString().equals(s)) {
                    view.setText(s);
                }
            }
        }, view, newValue);
    }
}
