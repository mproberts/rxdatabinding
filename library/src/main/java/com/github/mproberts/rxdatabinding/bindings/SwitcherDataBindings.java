package com.github.mproberts.rxdatabinding.bindings;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;

import com.github.mproberts.rxdatabinding.BR;
import com.github.mproberts.rxdatabinding.tools.DataBindingTools;
import com.github.mproberts.rxdatabinding.tools.UiThreadScheduler;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class SwitcherDataBindings {

    private static class LayoutViewFactory implements ViewSwitcher.ViewFactory {

        private final int _layoutRes;
        private final Context _context;

        public LayoutViewFactory(@LayoutRes int layoutRes, Context context) {
            _layoutRes = layoutRes;
            _context = context;
        }

        @Override
        public View makeView() {

            LayoutInflater inflater = LayoutInflater.from(_context);

            return inflater.inflate(_layoutRes, null);
        }
    }

    private SwitcherDataBindings() {
    }

    public interface ViewFactoryCreator {

        ViewSwitcher.ViewFactory getFactory(Context context);
    }

    @BindingAdapter(value = {"stringList", "rate"})
    public static void bindTextSwitcherList(final TextSwitcher view, int stringListRes, long rate) {
        final String[] strings = view.getContext().getResources().getStringArray(stringListRes);

        Flowable<String> stringRate = Flowable.interval(0, rate, TimeUnit.MILLISECONDS)
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(Long period) throws Exception {
                        return strings[(int) (period % strings.length)];
                    }
                });

        bindTextSwitcherText(view, stringRate);
    }

    @BindingAdapter(value = {"android:text"})
    public static void bindTextSwitcherText(final TextSwitcher view, Flowable<String> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.text, new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                view.setText(s);
            }
        }, view, newValue);
    }

    @BindingAdapter(value = {"model"})
    public static void bindSwitcherModel(final ViewSwitcher view, Flowable newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.value, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                View nextView = view.getNextView();
                ViewDataBinding binding = DataBindingUtil.getBinding(nextView);

                binding.setVariable(BR.model, o);

                view.showNext();
            }
        }, view, newValue);
    }

    @BindingAdapter(value = {"factory"})
    public static void bindSwitcherFactory(final ViewSwitcher view, ViewFactoryCreator factory) {
        view.setFactory(factory.getFactory(view.getContext()));
    }

    @BindingAdapter(value = {"factory"})
    public static void bindSwitcherFactory(final ViewSwitcher view, ViewSwitcher.ViewFactory factory) {
        view.setFactory(factory);
    }

    @BindingAdapter(value = {"layout"})
    public static void bindSwitcherLayout(final ViewSwitcher view, @LayoutRes int layout) {
        bindSwitcherFactory(view, new LayoutViewFactory(layout, view.getContext()));
    }
}
