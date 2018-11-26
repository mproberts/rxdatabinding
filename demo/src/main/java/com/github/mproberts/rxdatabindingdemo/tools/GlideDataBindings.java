package com.github.mproberts.rxdatabindingdemo.tools;

import androidx.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.github.mproberts.rxdatabinding.tools.DataBindingTools;
import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;

public final class GlideDataBindings {
    private GlideDataBindings() {
    }

    @BindingAdapter("photoUrl")
    public static void bindOptionalGlidePhotoUrl(final ImageView view, Flowable<Optional<String>> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.src, (url) -> {
            if (url.isPresent()) {
                GlideApp
                        .with(view.getContext())
                        .load(url.get())
                        .circleCrop()
                        .into(view);
            } else {
                view.setImageDrawable(null);
            }
        }, view, newValue, null, null);
    }

    @BindingAdapter(value = {"photoUrl", "placeholder"})
    public static void bindGlidePhotoUrl(final ImageView view, Flowable<Optional<String>> newValue, final Drawable placeholder) {
        DataBindingTools.bindViewProperty(android.R.attr.src, (url) -> {
            if (url.isPresent()) {
                GlideApp
                        .with(view.getContext())
                        .load(url.get())
                        .circleCrop()
                        .fallback(placeholder)
                        .placeholder(placeholder)
                        .into(view);
            } else {
                view.setImageDrawable(placeholder);
            }
        }, view, newValue, null, null);
    }
}
