package com.github.mproberts.rxdatabinding.bindings;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mproberts.rxdatabinding.BR;

import java.util.ArrayList;
import java.util.List;

public class ViewBuilder {
    private static final int TAG_DATABINDING_CREATOR
            = "com.github.mproberts.rxdatabinding.bindings.ViewBuilder".hashCode();

    interface Matcher {
        boolean matches(Object model);
    }

    interface Creator {
        void bind(Context context, View view, Object model);

        View createView(Context context, LayoutInflater inflater, ViewGroup parent);
    }

    private static class AlwaysMatcher implements Matcher {

        private static final AlwaysMatcher INSTANCE = new AlwaysMatcher();

        @Override
        public boolean matches(Object model) {
            return true;
        }
    }

    private static class TypedMatcher implements Matcher {

        private final Class<?> clzz;

        private TypedMatcher(Class<?> clzz) {
            this.clzz = clzz;
        }

        @Override
        public boolean matches(Object model) {
            Class<?> clazz = model.getClass();

            return clzz.isAssignableFrom(clazz);
        }
    }

    private static final class DataBindingCreator implements Creator {
        private final int layoutId;

        private DataBindingCreator(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
        }

        @Override
        public void bind(Context context, View view, Object model) {
            // rebind the provided view
            ViewDataBinding binding = DataBindingUtil.getBinding(view);

            if (binding != null) {
                binding.setVariable(BR.model, model);
                binding.executePendingBindings();
            }
        }

        @Override
        public View createView(Context context, LayoutInflater inflater, ViewGroup parent) {
            return DataBindingUtil.inflate(inflater, layoutId, parent, false).getRoot();
        }
    }

    private final class BindingGroup {
        final Matcher matcher;
        final Creator creator;

        private BindingGroup(Matcher matcher, Creator creator) {
            this.matcher = matcher;
            this.creator = creator;
        }
    }

    private final List<BindingGroup> _bindings = new ArrayList<>();

    public int findMatch(Object model) {
        for (int i = 0, l = _bindings.size(); i < l; ++i) {
            BindingGroup bindingGroup = _bindings.get(i);

            if (bindingGroup.matcher.matches(model)) {
                return i;
            }
        }

        throw new UnsupportedOperationException("No builder for type " + model.getClass().getCanonicalName());
    }

    public void bind(Context context, View view, Object model) {
        BindingGroup matchedGroup = (BindingGroup) view.getTag(TAG_DATABINDING_CREATOR);

        matchedGroup.creator.bind(context, view, model);
    }

    public View createView(Context context, LayoutInflater inflater, ViewGroup parent, Object model) {
        return createView(context, inflater, parent, findMatch(model));
    }

    public View createView(Context context, LayoutInflater inflater, ViewGroup parent, int index) {
        BindingGroup matchedGroup = _bindings.get(index);
        View view = matchedGroup.creator.createView(context, inflater, parent);

        view.setTag(TAG_DATABINDING_CREATOR, matchedGroup);

        return view;
    }

    public ViewBuilder add(Matcher matcher, Creator creator) {
        _bindings.add(new BindingGroup(matcher, creator));

        return this;
    }

    public ViewBuilder add(Creator creator) {
        _bindings.add(new BindingGroup(AlwaysMatcher.INSTANCE, creator));

        return this;
    }

    public ViewBuilder add(Class<?> clazz, Creator creator) {
        return add(new TypedMatcher(clazz), creator);
    }

    public ViewBuilder add(Class<?> clazz, @LayoutRes int layoutId) {
        return add(clazz, new DataBindingCreator(layoutId));
    }

    public ViewBuilder add(@LayoutRes int layoutId) {
        return add(new DataBindingCreator(layoutId));
    }
}
