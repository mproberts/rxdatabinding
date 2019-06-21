package com.github.mproberts.rxdatabinding.bindings;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mproberts.rxdatabinding.BR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

public interface ViewCreator<T, TView extends View> {
    int findType(T model);

    void bind(Context context, TView view, T model, int layoutType, CompositeDisposable lifecycle);

    View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType);

    boolean recycle(TView view, int layoutType);

    interface MatchingViewCreator<T, TView extends View> extends ViewCreator<T, TView> {
        boolean matches(T model);
    }

    public static class Creator implements ViewCreator {

        private List<MatchingViewCreator> _bindings = new ArrayList<>();

        private static final int BINDING_INDEX_TAG = "Creator.BINDING_INDEX_TAG".hashCode();

        public Creator() {
        }

        protected Creator(List<MatchingViewCreator> bindings) {
            _bindings.addAll(bindings);
        }

        protected int find(Object model) {
            for (int i = 0, s = _bindings.size(); i < s; ++i) {
                MatchingViewCreator viewBinding = _bindings.get(i);

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
            MatchingViewCreator binding = _bindings.get(getWrappedIndex(layoutType));

            binding.bind(context, view, model, layoutType, lifecycle);
        }

        @Override
        public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
            MatchingViewCreator binding = _bindings.get(getWrappedIndex(layoutType));

            View view = binding.create(context, inflater, parent, layoutType);

            return view;
        }

        @Override
        public boolean recycle(View view, int layoutType) {
            MatchingViewCreator binding = _bindings.get(getWrappedIndex(layoutType));

            return binding.recycle(view, layoutType);
        }

        private int getWrappedIndex(int layoutType) {
            return layoutType & 0x000000ff;
        }

        private int fromWrappedLayoutType(int layoutType) {
            return (layoutType & 0xffffff00) >> 8;
        }

        private int toWrappedLayoutType(int layoutType, int index) {
            return layoutType << 8 | index;
        }

        public Creator add(final MatchingViewCreator binding) {
            final int currentIndex = _bindings.size();

            _bindings.add(new MatchingViewCreator() {
                @Override
                public boolean matches(Object model) {
                    return binding.matches(model);
                }

                @Override
                public int findType(Object model) {
                    return toWrappedLayoutType(binding.findType(model), currentIndex);
                }

                @Override
                public void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle) {
                    binding.bind(context, view, model, fromWrappedLayoutType(layoutType), lifecycle);
                }

                @Override
                public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
                    return binding.create(context, inflater, parent, fromWrappedLayoutType(layoutType));
                }

                @Override
                public boolean recycle(View view, int layoutType) {
                    return binding.recycle(view, fromWrappedLayoutType(layoutType));
                }
            });

            return this;
        }

        public <T> Creator add(Class<T> clzz, final ViewCreator binding) {
            final int currentIndex = _bindings.size();

            _bindings.add(new TypeMatchingViewCreator<T>(clzz) {
                @Override
                public int findType(Object model) {
                    return toWrappedLayoutType(binding.findType(model), currentIndex);
                }

                @Override
                public void bind(Context context, View view, Object model, int layoutType, CompositeDisposable lifecycle) {
                    binding.bind(context, view, model, fromWrappedLayoutType(layoutType), lifecycle);
                }

                @Override
                public View create(Context context, LayoutInflater inflater, ViewGroup parent, int layoutType) {
                    return binding.create(context, inflater, parent, fromWrappedLayoutType(layoutType));
                }

                @Override
                public boolean recycle(View view, int layoutType) {
                    return binding.recycle(view, fromWrappedLayoutType(layoutType));
                }
            });

            return this;
        }

        public ViewCreator cached(int cacheSize) {
            return new CachingCreator(this, cacheSize);
        }
    }

    public static class CompositeCreator extends Creator {
        private final ViewCreator _innerBinding;

        private static final int CHILD_BINDING_TAG = "Creator.CHILD_BINDING_TAG".hashCode();

        public CompositeCreator(ViewCreator innerBinding) {
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

class CachingCreator implements ViewCreator {

    private final ViewCreator _innerBuilder;
    private final int _cacheSize;
    private final Map<Integer, List<View>> _cache = new HashMap<>();

    CachingCreator(ViewCreator innerBuilder, int cacheSize) {
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
