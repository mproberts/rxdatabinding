package com.github.mproberts.rxdatabinding.bindings;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.github.mproberts.rxdatabinding.tools.DataBindingTools;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public final class ViewDataBindings {
    private ViewDataBindings() {
    }

    public interface StringConsumer extends Consumer<String> {
    }

    @BindingAdapter(value = {"android:background", "colorLevel"})
    public static void bindAndroidLevelListColor(final View view, Drawable drawable, Flowable<Integer> newValue) {
        if (!(drawable instanceof LevelListDrawable)) {
            return;
        }

        final LevelListDrawable levelListDrawable = (LevelListDrawable) drawable;

        view.setBackground(levelListDrawable);

        DataBindingTools.bindViewProperty(android.R.attr.background, new Consumer<Integer>() {
            @Override
            public void accept(Integer level) throws Exception {
                levelListDrawable.setLevel(level);
            }
        }, view, newValue);
    }

    @BindingAdapter("android:visibility")
    public static void bindAndroidVisibility(final View view, Flowable<Boolean> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.visibility, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) throws Exception {
                view.setVisibility(value ? View.VISIBLE : View.GONE);
            }
        }, view, newValue);
    }

    @BindingAdapter("android:visibility")
    public static void bindAndroidVisibilityString(final View view, Flowable<String> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.visibility, new Consumer<String>() {
            @Override
            public void accept(String value) throws Exception {
                view.setVisibility(value.length() != 0 ? View.VISIBLE : View.GONE);
            }
        }, view, newValue);
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

    @BindingAdapter("android:layout_marginTop")
    public static void bindAndroidLayoutMarginTop(final View view, Flowable<Float> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.layout_marginTop, new Consumer<Float>() {
            @Override
            public void accept(Float value) throws Exception {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;

                    marginLayoutParams.topMargin = value.intValue();
                }
            }
        }, view, newValue);
    }

    @BindingAdapter("android:enabled")
    public static void bindAndroidEnabled(final View view, Flowable<Boolean> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.enabled, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) throws Exception {
                view.setEnabled(value);
            }
        }, view, newValue);
    }

    @BindingAdapter("android:onClick")
    public static void bindAndroidOnClick(final View view, final Runnable listener) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.run();
            }
        });
    }

    @BindingAdapter("textChanged")
    public static void bindTextWatcher(final EditText view, final StringConsumer newValue) {
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    newValue.accept(view.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // noop
            }
        });
    }

    @BindingConversion
    public static View.OnClickListener onClickActionConversion(final Action listener) {
        if (listener == null) {
            return null;
        }

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.run();
                } catch (Exception e) {
                    DataBindingTools.handleError(e);
                }
            }
        };
    }

    @BindingConversion
    public static View.OnClickListener onClickRunnableConversion(final Runnable listener) {
        if (listener == null) {
            return null;
        }

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.run();
            }
        };
    }
}
