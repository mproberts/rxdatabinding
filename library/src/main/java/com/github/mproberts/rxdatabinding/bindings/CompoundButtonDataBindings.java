package com.github.mproberts.rxdatabinding.bindings;

import androidx.databinding.BindingAdapter;
import android.widget.CompoundButton;

import com.github.mproberts.rxdatabinding.tools.DataBindingTools;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

public final class CompoundButtonDataBindings {
    private CompoundButtonDataBindings() {
    }

    public interface BooleanConsumer extends Consumer<Boolean> {
    }

    @BindingAdapter("android:checked")
    public static void bindAndroidChecked(final CompoundButton view, Flowable<Boolean> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.checked, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) throws Exception {
                view.setChecked(value);
            }
        }, view, newValue);
    }

    @BindingAdapter("onCheckedChanged")
    public static void bindAndroidChecked(final CompoundButton view, final BooleanConsumer onChecked) {
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                try {
                    onChecked.accept(isChecked);
                } catch (Exception e) {
                    DataBindingTools.handleError(e);
                }
            }
        });
    }

}
