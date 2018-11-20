package com.github.mproberts.rxdatabinding.bindings;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.mproberts.rxdatabinding.BR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

public interface ViewBuilder<T, TView extends View> {
    int findType(T model);

    void bind(Context context, TView view, T model, int layoutType, CompositeDisposable lifecycle);

    View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType);

    boolean recycle(TView view, int layoutType);

    interface MatchingViewBuilder<T, TView extends View> extends ViewBuilder<T, TView> {
        boolean matches(T model);
    }

    public static class Builder implements ViewBuilder {

        private List<MatchingViewBuilder> _bindings = new ArrayList<>();

        private static final int BINDING_INDEX_TAG = "Builder.BINDING_INDEX_TAG".hashCode();

        public Builder() {
        }

        protected Builder(List<MatchingViewBuilder> bindings) {
            _bindings.addAll(bindings);
        }

        protected int find(Object model) {
            for (int i = 0, s = _bindings.size(); i < s; ++i) {
                MatchingViewBuilder viewBinding = _bindings.get(i);

                if (viewBinding.matches(model)) {
                    return i;
                }
            }

            throw new IllegalArgumentException("No view binding found for object " + model.toString());
        }

        @Override
        public int findType(Object model) {
            return find(model);
        }

        @Override
        public void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle) {
            MatchingViewBuilder binding = _bindings.get(layoutType);

            binding.bind(context, view, model, layoutType, lifecycle);
        }

        @Override
        public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
            MatchingViewBuilder binding = _bindings.get(layoutType);

            View view = binding.create(context, inflater, parent, layoutType);

            return view;
        }

        @Override
        public boolean recycle(View view, int layoutType) {
            MatchingViewBuilder binding = _bindings.get(layoutType);

            return binding.recycle(view, layoutType);
        }

        public Builder add(MatchingViewBuilder binding) {
            _bindings.add(binding);

            return this;
        }

        public <T> Builder add(Class<T> clzz, final ViewBuilder binding) {
            _bindings.add(new TypeMatchingViewBuilder<T>(clzz) {
                @Override
                public int findType(Object model) {
                    return binding.findType(model);
                }

                @Override
                public void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle) {
                    binding.bind(context, view, model, layoutType, lifecycle);
                }

                @Override
                public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
                    return binding.create(context, inflater, parent, layoutType);
                }

                @Override
                public boolean recycle(View view, int layoutType) {
                    return binding.recycle(view, layoutType);
                }
            });

            return this;
        }

        public ViewBuilder cached(int cacheSize) {
            return new CachingBuilder(this, cacheSize);
        }
    }

    public static class CompositeBuilder extends ViewBuilder.Builder {
        private final ViewBuilder _innerBinding;

        private static final int CHILD_BINDING_TAG = "Builder.CHILD_BINDING_TAG".hashCode();

        public CompositeBuilder(ViewBuilder innerBinding) {
            _innerBinding = innerBinding;
        }

        private int getInnerType(int layoutType) {
            return layoutType & 0xff;
        }

        private int getOuterType(int layoutType) {
            return (layoutType & ~0xff) >> 8;
        }

        @Override
        public int findType(Object model) {
            int innerType = _innerBinding.findType(model);
            int outerType = super.findType(model);

            return (outerType << 8) | innerType;
        }

        @Override
        public void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle) {
            int outerType = getOuterType(layoutType);
            int innerType = getInnerType(layoutType);

            super.bind(context, view, model, outerType, lifecycle);

            View childView = (View) view.getTag(CHILD_BINDING_TAG);

            _innerBinding.bind(context, childView, model, innerType, lifecycle);
        }

        @Override
        public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
            int outerType = getOuterType(layoutType);
            int innerType = getInnerType(layoutType);

            ViewGroup wrapper = (ViewGroup) super.create(context, inflater, parent, outerType);
            View child = _innerBinding.create(context, inflater, wrapper, innerType);

            wrapper.addView(child);
            wrapper.setTag(CHILD_BINDING_TAG, child);

            return wrapper;
        }

        @Override
        public boolean recycle(View view, int layoutType) {
            int outerType = getOuterType(layoutType);
            int innerType = getInnerType(layoutType);

            View childView = (View) view.getTag(CHILD_BINDING_TAG);

            _innerBinding.recycle(childView, innerType);

//            ViewParent parent = childView.getParent();

//            if (parent != null) {
//                ((ViewGroup) parent).removeView(childView);
//            }

            return super.recycle(view, outerType);
        }
    }
}

class LayoutViewBuilder<T, TView extends View> implements ViewBuilder<T, TView> {

    private final int _layoutId;

    LayoutViewBuilder(int layoutId) {
        _layoutId = layoutId;
    }

    @Override
    public int findType(T model) {
        return _layoutId;
    }

    @Override
    public void bind(Context context, TView view, T model, int layoutType, CompositeDisposable lifecycle) {
        ViewDataBinding binding = DataBindingUtil.getBinding(view);
        binding.setVariable(BR.model, model);
        binding.executePendingBindings();
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

class CachingBuilder implements ViewBuilder {

    private final ViewBuilder _innerBuilder;
    private final int _cacheSize;
    private final Map<Integer, List<View>> _cache = new HashMap<>();

    CachingBuilder(ViewBuilder innerBuilder, int cacheSize) {
        _innerBuilder = innerBuilder;
        _cacheSize = cacheSize;
    }

    @Override
    public int findType(Object model) {
        return _innerBuilder.findType(model);
    }

    @Override
    public void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle) {
        _innerBuilder.bind(context, view, model, layoutType, lifecycle);
    }

    @Override
    public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
        List<View> cachedViews = _cache.get(layoutType);

        if (cachedViews != null && cachedViews.size() > 0) {
            return cachedViews.remove(0);
        }

        return _innerBuilder.create(context, inflater, parent, layoutType);
    }

    @Override
    public boolean recycle(View view, int layoutType) {
        List<View> cachedViews = _cache.get(layoutType);

        if (cachedViews == null) {
            cachedViews = new ArrayList<>();
            _cache.put(layoutType, cachedViews);
        }

        if (cachedViews.size() >= _cacheSize) {
            return false;
        }

        boolean recycled = _innerBuilder.recycle(view, layoutType);

        if (recycled) {
            cachedViews.add(view);

            return true;
        }

        return false;
    }
}
