package com.github.mproberts.rxdatabinding.bindings;

import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mproberts.rxdatabinding.BR;
import com.github.mproberts.rxdatabinding.tools.DataBindingTools;
import com.github.mproberts.rxdatabinding.tools.UiThreadScheduler;
import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.list.Update;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ViewPagerDataBindings {
    private ViewPagerDataBindings() {
    }

    public interface TabDelegate {

        class Tab {
            public final View customView;
            public final Drawable icon;
            public final boolean switchToTab;
            public final Runnable clickListener;

            public Tab(View customView, Drawable icon, boolean switchToTab, Runnable clickListener) {
                this.customView = customView;
                this.icon = icon;
                this.switchToTab = switchToTab;
                this.clickListener = clickListener;
            }
        }

        Context getContext();

        void updateTabs(List<Tab> tabs);

        void attachViewPager(ViewPager viewPager);
    }

    private static final int PAGER_ADAPTER_TAG = "ViewPagerDataBindings.PAGER_ADAPTER_TAG".hashCode();
    private static final int PAGER_TAB_LAYOUT = "ViewPagerDataBindings.PAGER_TAB_LAYOUT".hashCode();

    @BindingAdapter({"data", "itemLayoutCreator"})
    public static void bindList(final ViewPager viewPager, FlowableList<?> list, BindingPagerAdapter.ItemViewCreator layoutCreator) {
        final BindingPagerAdapter bindingPagerAdapter = new BindingPagerAdapter(new BasicItemDataProvider(list), layoutCreator);

        DataBindingTools.watchActivityAttachment(viewPager, new Consumer<Activity>() {
            @Override
            public void accept(Activity activity) throws Exception {
                if (activity != null) {
                    bindingPagerAdapter.subscribe(viewPager.getContext());
                } else {
                    bindingPagerAdapter.unsubscribe();
                }
            }
        });
        viewPager.setAdapter(bindingPagerAdapter);
        TabDelegate tabDelegate = (TabDelegate) viewPager.getTag(PAGER_TAB_LAYOUT);

        if (tabDelegate != null) {
            bindingPagerAdapter.setTabLayout(tabDelegate);
            tabDelegate.attachViewPager(viewPager);
        }

        viewPager.setTag(PAGER_ADAPTER_TAG, bindingPagerAdapter);
    }

    public static void associateTabLayout(ViewPager viewPager, TabDelegate tabDelegate) {
        BindingPagerAdapter bindingPagerAdapter = (BindingPagerAdapter) viewPager.getTag(PAGER_ADAPTER_TAG);

        viewPager.setTag(PAGER_TAB_LAYOUT, tabDelegate);
        tabDelegate.attachViewPager(viewPager);

        if (bindingPagerAdapter != null) {
            bindingPagerAdapter.setTabLayout(tabDelegate);
        }
    }

    private static class BasicItemDataProvider implements BindingPagerAdapter.ItemDataProvider {
        private final FlowableList<?> _list;

        private BasicItemDataProvider(FlowableList<?> list) {
            _list = list;
        }

        @Override
        public FlowableList<?> getList() {
            return _list;
        }
    }

    public static class SimpleLayoutItemViewCreator implements BindingPagerAdapter.ItemViewCreator {

        private final int _itemLayoutId;
        private final int _tabLayoutId;
        private Drawable _tabIcon;
        private final int _tabIconId;
        private final boolean _shouldSwitch;
        private final Consumer<?> _onClick;

        public SimpleLayoutItemViewCreator(int itemLayoutId, int tabLayoutId, Drawable tabIcon, int tabIconId, boolean shouldSwitch, Consumer<?> onClick) {
            _itemLayoutId = itemLayoutId;
            _tabLayoutId = tabLayoutId;
            _tabIcon = tabIcon;
            _tabIconId = tabIconId;
            _shouldSwitch = shouldSwitch;
            _onClick = onClick;
        }

        private Drawable getTabIcon(Context context, Object viewModel) {
            if (_tabIcon == null) {
                if (_tabIconId > 0) {
                    _tabIcon = context.getResources().getDrawable(_tabIconId);
                }
            }
            return _tabIcon;
        }

        @Override
        public TabDelegate.Tab createTab(LayoutInflater inflater, Context context, final Object viewModel) {
            View customView = null;
            Drawable icon = getTabIcon(context, viewModel);

            if (_tabLayoutId > 0) {
                ViewDataBinding binding = DataBindingUtil.inflate(inflater, _tabLayoutId, null, false);

                binding.setVariable(BR.model, viewModel);

                customView = binding.getRoot();
            }

            return new TabDelegate.Tab(customView, icon, _shouldSwitch, new Runnable() {
                @Override
                @SuppressWarnings("unchecked")
                public void run() {
                    try {
                        ((Consumer<Object>) _onClick).accept(viewModel);
                    } catch (Exception e) {
                        RxJavaPlugins.onError(e);
                    }
                }
            });
        }

        @Override
        public View createItemLayout(LayoutInflater inflater, ViewGroup parent, Object viewModel) {
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, _itemLayoutId, parent, true);

            binding.setVariable(BR.model, viewModel);

            return binding.getRoot();
        }
    }

    public static class TypedLayoutCreator implements BindingPagerAdapter.ItemViewCreator {
        private Map<Class<?>, BindingPagerAdapter.ItemViewCreator> _layouts = new HashMap();

        public TypedLayoutCreator addLayout(@LayoutRes int layoutId, @LayoutRes int tabLayoutId, Class<?> clazz) {
            _layouts.put(clazz, new SimpleLayoutItemViewCreator(layoutId, tabLayoutId, null, 0, true, null));

            return this;
        }

        public TypedLayoutCreator addLayoutWithIcon(@LayoutRes int layoutId, @DrawableRes int iconId, Class<?> clazz) {
            _layouts.put(clazz, new SimpleLayoutItemViewCreator(layoutId, 0, null, iconId, true, null));

            return this;
        }

        public TypedLayoutCreator addLayoutWithIcon(@LayoutRes int layoutId, @DrawableRes int iconId, boolean shouldSwitchToTab, Class<?> clazz) {
            _layouts.put(clazz, new SimpleLayoutItemViewCreator(layoutId, 0, null, iconId, shouldSwitchToTab, null));

            return this;
        }

        public <T> TypedLayoutCreator addLayoutWithIcon(@LayoutRes int layoutId, @DrawableRes int iconId, boolean shouldSwitchToTab, Class<?> clazz, Consumer<T> onClick) {
            _layouts.put(clazz, new SimpleLayoutItemViewCreator(layoutId, 0, null, iconId, shouldSwitchToTab, null));

            return this;
        }

        public TypedLayoutCreator addLayoutWithIcon(@LayoutRes int layoutId, Drawable icon, Class<?> clazz) {
            _layouts.put(clazz, new SimpleLayoutItemViewCreator(layoutId, 0, icon, 0, true, null));

            return this;
        }

        @Override
        public TabDelegate.Tab createTab(LayoutInflater inflater, Context context, Object viewModel) {
            BindingPagerAdapter.ItemViewCreator itemLayoutCreator = getItemLayout(viewModel);

            return itemLayoutCreator.createTab(inflater, context, viewModel);
        }

        @Override
        public View createItemLayout(LayoutInflater inflater, ViewGroup parent, Object viewModel) {
            BindingPagerAdapter.ItemViewCreator itemLayoutCreator = getItemLayout(viewModel);

            return itemLayoutCreator.createItemLayout(inflater, parent, viewModel);
        }

        private BindingPagerAdapter.ItemViewCreator getItemLayout(Object viewModel) {
            Class<?> clazz = viewModel.getClass();

            for (Map.Entry<Class<?>, BindingPagerAdapter.ItemViewCreator> layoutEntry : _layouts.entrySet()) {
                if (layoutEntry.getKey().isAssignableFrom(clazz)) {
                    return layoutEntry.getValue();
                }
            }

            return null;
        }
    }

    private static class BindingPagerAdapter extends PagerAdapter {

        public interface ItemDataProvider {
            FlowableList<?> getList();
        }

        private class PageHolder {
            final View root;
            final Object viewModel;

            private PageHolder(View root, Object viewModel) {
                this.root = root;
                this.viewModel = viewModel;
            }
        }

        public interface ItemViewCreator {
            TabDelegate.Tab createTab(LayoutInflater inflater, Context context, Object viewModel);

            View createItemLayout(LayoutInflater inflater, ViewGroup parent, Object viewModel);
        }

        private LayoutInflater _cachedLayoutInflater;
        private ItemDataProvider _dataProvider;
        private ItemViewCreator _viewCreator;
        private List<?> _currentState;
        private Disposable _subscription;
        private TabDelegate _tabDelegate;

        private LayoutInflater getCachedLayoutInflater(Context context) {
            if (_cachedLayoutInflater == null) {
                _cachedLayoutInflater = LayoutInflater.from(context);
            }
            return _cachedLayoutInflater;
        }

        BindingPagerAdapter(ItemDataProvider dataProvider, ItemViewCreator viewCreator) {
            _dataProvider = dataProvider;
            _viewCreator = viewCreator;
        }

        public void setTabLayout(TabDelegate tabDelegate) {
            _tabDelegate = tabDelegate;

            if (_tabDelegate != null && _currentState != null) {
                updateTabs(tabDelegate.getContext(), _currentState);
            }
        }

        @Override
        public int getCount() {
            return _currentState == null ? 0 : _currentState.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            if (!(object instanceof PageHolder)) {
                return false;
            }
            return ((PageHolder) object).root == view;
        }

        void updateTabs(Context context, List<?> list) {
            if (_tabDelegate != null) {
                List<TabDelegate.Tab> tabs = new ArrayList<>();

                for (int i = 0; i < list.size(); ++i) {
                    Object viewModel = list.get(i);
                    TabDelegate.Tab tab = createTab(context, viewModel);

                    if (tab != null) {
                        tabs.add(tab);
                    }
                }

                _tabDelegate.updateTabs(tabs);
            }
        }

        public TabDelegate.Tab createTab(Context context, Object viewModel) {
            if (_tabDelegate == null) {
                return null;
            }

            LayoutInflater inflater = getCachedLayoutInflater(context);

            return _viewCreator.createTab(inflater, context, viewModel);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object viewModel = _currentState.get(position);

            LayoutInflater inflater = _cachedLayoutInflater;

            if (inflater == null) {
                _cachedLayoutInflater = LayoutInflater.from(container.getContext());
                inflater = _cachedLayoutInflater;
            }

            View rootView = _viewCreator.createItemLayout(inflater, container, viewModel);

            return new PageHolder(rootView, viewModel);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
        }

        public void unsubscribe() {
            Disposable subscription = _subscription;

            _subscription = null;

            if (subscription != null) {
                subscription.dispose();
            }
        }

        public void subscribe(final Context context) {
            final FlowableList<?> list = _dataProvider.getList();

            unsubscribe();

            if (list != null) {
                _subscription = list.updates().observeOn(UiThreadScheduler.uiThread()).subscribe(new Consumer<Update<?>>() {
                    public void accept(Update<?> update) throws Exception {
                        BindingPagerAdapter.this._currentState = update.list;
                        notifyDataSetChanged();

                        updateTabs(context, update.list);
                    }
                });
            }
        }
    }
}
