package com.github.mproberts.rxdatabinding.bindings;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

public interface ViewBinding {
    int findType(Object model);

    void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle);

    View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType);

    boolean recycle(View view, int layoutType);

    interface MatchingViewBinding extends ViewBinding {
        boolean matches(Object model);
    }

    class Builder implements ViewBinding {

        private List<MatchingViewBinding> _bindings = new ArrayList<>();

        private static final int BINDING_INDEX_TAG = "Builder.BINDING_INDEX_TAG".hashCode();

        public Builder() {
        }

        protected Builder(List<MatchingViewBinding> bindings) {
            _bindings.addAll(bindings);
        }

        protected int find(Object model) {
            for (int i = 0, s = _bindings.size(); i < s; ++i) {
                MatchingViewBinding viewBinding = _bindings.get(i);

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
            MatchingViewBinding binding = _bindings.get(layoutType);

            binding.bind(context, view, model, layoutType, lifecycle);
        }

        @Override
        public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
            MatchingViewBinding binding = _bindings.get(layoutType);

            View view = binding.create(context, inflater, parent, layoutType);

            return view;
        }

        @Override
        public boolean recycle(View view, int layoutType) {
            MatchingViewBinding binding = _bindings.get(layoutType);

            return binding.recycle(view, layoutType);
        }

        public Builder add(MatchingViewBinding binding) {
            _bindings.add(binding);

            return this;
        }

        public <T> Builder add(Class<T> clzz, final ViewBinding binding) {
            _bindings.add(new TypeMatchingViewBinding<T>(clzz) {
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

        public Builder cached(int cacheSize) {
            return new CachingBuilder(_bindings, cacheSize);
        }
    }

    class CompositeBuilder extends ViewBinding.Builder {
        private final ViewBinding _innerBinding;

        private static final int CHILD_BINDING_TAG = "Builder.CHILD_BINDING_TAG".hashCode();

        public CompositeBuilder(ViewBinding innerBinding) {
            _innerBinding = innerBinding;
        }

        @Override
        public int findType(Object model) {
            int innerType = _innerBinding.findType(model);
            int outerType = super.findType(model);

            return (outerType << 8) | innerType;
        }

        @Override
        public void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle) {
            super.bind(context, view, model, layoutType, lifecycle);

            View childView = (View) view.getTag(CHILD_BINDING_TAG);

            _innerBinding.bind(context, childView, model, layoutType, lifecycle);
        }

        @Override
        public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
            ViewGroup wrapper = (ViewGroup) super.create(context, inflater, parent, layoutType);
            View child = _innerBinding.create(context, inflater, wrapper, layoutType);

            wrapper.addView(child);
            wrapper.setTag(CHILD_BINDING_TAG, child);

            return wrapper;
        }

        @Override
        public boolean recycle(View view, int layoutType) {
            View childView = (View) view.getTag(CHILD_BINDING_TAG);

            _innerBinding.recycle(childView, layoutType);

            return super.recycle(view, layoutType);
        }
    }
}

class LayoutViewBinding implements ViewBinding {

    private final int _layoutId;

    LayoutViewBinding(int layoutId) {
        _layoutId = layoutId;
    }

    @Override
    public int findType(Object model) {
        return _layoutId;
    }

    @Override
    public void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle) {

    }

    @Override
    public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
        return DataBindingUtil.inflate(inflater, _layoutId, parent, false).getRoot();
    }

    @Override
    public boolean recycle(View view, int layoutType) {
        return false;
    }
}

class CachingBuilder extends ViewBinding.Builder {

    private final int _cacheSize;
    private final Map<Integer, View> _cache = new HashMap<>();

    CachingBuilder(List<MatchingViewBinding> bindings, int cacheSize) {
        super(bindings);

        _cacheSize = cacheSize;
    }

    @Override
    public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
        if (_cache.size() > 0) {
            return _cache.remove(0);
        }

        return super.create(context, inflater, parent, layoutType);
    }

    @Override
    public boolean recycle(View view, int layoutType) {
        if (_cache.size() >= _cacheSize) {
            return false;
        }

        boolean recycled = super.recycle(view, layoutType);

        if (recycled) {
            _cache.put(layoutType, view);

            return true;
        }

        return false;
    }
}
