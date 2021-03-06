package com.github.mproberts.rxdatabinding.bindings;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mproberts.rxdatabinding.BR;
import com.github.mproberts.rxdatabinding.R;
import com.github.mproberts.rxdatabinding.internal.Lifecycle;
import com.github.mproberts.rxdatabinding.internal.MutableLifecycle;
import com.github.mproberts.rxdatabinding.internal.WindowAttachLifecycle;
import com.github.mproberts.rxdatabinding.tools.DataBindingTools;
import com.github.mproberts.rxdatabinding.tools.UiThreadScheduler;
import com.github.mproberts.rxtools.list.Change;
import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.list.SimpleFlowableList;
import com.github.mproberts.rxtools.list.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class RecyclerViewDataBindings {
    private RecyclerViewDataBindings() {
    }

    /**
     * RecyclerViewBindingSink can be provided through a binding adapter in order to notify a
     * fragment or activity that a recycler view was bound. This can be used when a recycler view's
     * data should be bound even when it is removed from the window.
     * Recycler views using a binding sink are only bound once and should handle being detached
     * manually.
     */
    public interface RecyclerViewBindingSink {
        void notifyRecyclerViewBound(RecyclerView recyclerView);
    }

    interface LayoutManagerCreator extends Callable<RecyclerView.LayoutManager> {
    }

    @BindingAdapter(value = {"data", "itemLayout"})
    public static void bindList(RecyclerView recyclerView, FlowableList<?> list, @LayoutRes int layoutId) {
        recyclerView.setAdapter(new RecyclerViewAdapter(list, new BasicLayoutCreator(layoutId), null));
    }

    @BindingAdapter(value = {"persistentData", "itemLayout", "bindingSink"})
    public static void bindList(final RecyclerView recyclerView, final FlowableList<?> list, final @LayoutRes int layoutId, final RecyclerViewBindingSink bindingSink) {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new RecyclerViewAdapter(list, new BasicLayoutCreator(layoutId), bindingSink));
        }
    }

    @BindingAdapter(value = {"data", "itemLayoutCreator"})
    public static void bindList(RecyclerView recyclerView, FlowableList<?> list, RecyclerViewAdapter.ItemViewCreator layoutCreator) {
        recyclerView.setAdapter(new RecyclerViewAdapter(list, layoutCreator, null));
    }

    @BindingAdapter(value = {"persistentData", "itemLayoutCreator", "bindingSink"})
    public static void bindList(final RecyclerView recyclerView, final FlowableList<?> list, final RecyclerViewAdapter.ItemViewCreator layoutCreator, final RecyclerViewBindingSink bindingSink) {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new RecyclerViewAdapter(list, layoutCreator, bindingSink));
        }
    }

    private static RecyclerViewAdapter.ItemViewCreator itemViewCreatorFromViewCreator(final RecyclerView recyclerView, final ViewCreator viewCreator) {
        return new RecyclerViewAdapter.ItemViewCreator() {
            @Override
            public void bind(Object viewModel, RecyclerViewAdapter.ViewHolder holder) {
                ItemViewHolder viewHolder = (ItemViewHolder) holder;

                viewCreator.bind(recyclerView.getContext(), holder.itemView, viewModel, viewHolder.layoutType, viewHolder.disposable);
            }

            @Override
            public void recycled(RecyclerViewAdapter.ViewHolder holder) {
                super.recycled(holder);

                ItemViewHolder viewHolder = (ItemViewHolder) holder;

                viewCreator.recycle(holder.itemView, viewHolder.layoutType);
            }

            @Override
            public int getItemLayoutType(Object viewModel) {
                return viewCreator.findType(viewModel);
            }

            @Override
            public Object createItemLayout(LayoutInflater inflater, ViewGroup parent, int layoutType) {
                viewCreator.create(inflater.getContext(), inflater, parent, layoutType);

                return new ItemViewHolder(viewCreator.create(inflater.getContext(), inflater, parent, layoutType), layoutType) {
                    @Override
                    public View bind(Object viewModel, int layoutType) {
                        viewCreator.bind(itemView.getContext(), itemView, viewModel, layoutType, disposable);

                        return itemView;
                    }
                };
            }
        };
    }

    @BindingAdapter(value = {"data", "builder"})
    public static void bindList(final RecyclerView recyclerView, FlowableList<?> list, final ViewCreator viewCreator) {
        recyclerView.setAdapter(new RecyclerViewAdapter(list, itemViewCreatorFromViewCreator(recyclerView, viewCreator), null));
    }

    @BindingAdapter(value = {"persistentData", "builder", "bindingSink"})
    public static void bindList(final RecyclerView recyclerView, final FlowableList<?> list, final ViewCreator viewCreator, final RecyclerViewBindingSink bindingSink) {
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new RecyclerViewAdapter(list, itemViewCreatorFromViewCreator(recyclerView, viewCreator), bindingSink));
        }
    }

    @BindingAdapter(value = {"layoutManager"})
    public static void bindListLayoutManager(RecyclerView recyclerView, LayoutManagerCreator managerCreator) {
        try {
            recyclerView.setLayoutManager(managerCreator.call());
        } catch (Exception e) {
            DataBindingTools.handleError(e);
        }
    }

    public static class BasicLayoutCreator extends RecyclerViewAdapter.ItemViewCreator {
        private final int _layoutId;

        public BasicLayoutCreator(@LayoutRes int layoutId) {
            _layoutId = layoutId;
        }

        @Override
        public int getItemLayoutType(Object viewModel) {
            return 1;
        }

        @Override
        public Object createItemLayout(LayoutInflater inflater, ViewGroup parent, int layoutType) {
            View contentView = DataBindingUtil.inflate(inflater, _layoutId, parent, false).getRoot();

            return new ItemViewHolder(contentView, layoutType);
        }
    }

    public static abstract class PredicateLayoutCreator<T> extends RecyclerViewAdapter.ItemViewCreator {
        @LayoutRes
        public abstract int getLayoutResource(T model);

        @Override
        public final int getItemLayoutType(Object viewModel) {
            @SuppressWarnings("unchecked")
            int layoutResource = getLayoutResource((T) viewModel);

            return layoutResource;
        }

        @Override
        public final Object createItemLayout(LayoutInflater inflater, ViewGroup parent, int layoutType) {
            View contentView = DataBindingUtil.inflate(inflater, layoutType, parent, false).getRoot();

            return new ItemViewHolder(contentView, layoutType);
        }
    }

    public static class TypedLayoutCreator extends RecyclerViewAdapter.ItemViewCreator {
        
        private Map<Class<?>, Integer> _layouts = new HashMap<>();

        public TypedLayoutCreator addLayout(@LayoutRes int layoutId, Class<?> clazz) {
            _layouts.put(clazz, layoutId);

            return this;
        }

        @Override
        public final int getItemLayoutType(Object viewModel) {
            Class<?> clazz = viewModel.getClass();

            for (Map.Entry<Class<?>, Integer> layoutEntry : _layouts.entrySet()) {
                if (layoutEntry.getKey().isAssignableFrom(clazz)) {
                    return layoutEntry.getValue();
                }
            }

            return 0;
        }

        @Override
        public final Object createItemLayout(LayoutInflater inflater, ViewGroup parent, int layoutType) {
            View contentView = DataBindingUtil.inflate(inflater, layoutType, parent, false).getRoot();

            return new ItemViewHolder(contentView, layoutType);
        }
    }

    public static class SectionedLayoutCreator
            extends RecyclerViewAdapter.ItemViewCreator
            implements RecyclerViewAdapter.ItemDataProvider {

        private static class Section {
            private int _headerLayout;
            private FlowableList<?> _list;
            private RecyclerViewAdapter.ItemViewCreator _layoutCreator;

            public int getHeaderLayout() {
                return _headerLayout;
            }

            public RecyclerViewAdapter.ItemViewCreator getLayout() {
                return _layoutCreator;
            }

            public FlowableList<?> getList() {
                return _list;
            }

            public Section(int headerLayout, RecyclerViewAdapter.ItemViewCreator layoutCreator, FlowableList<?> list) {
                _headerLayout = headerLayout;
                _layoutCreator = layoutCreator;
                _list = list;
            }
        }

        private static class SectionItem<T> {
            private final Section _owner;
            private final T _value;

            public SectionItem(Section owner, T value) {
                _owner = owner;
                _value = value;
            }

            public Section getOwner() {
                return _owner;
            }

            public T getValue() {
                return _value;
            }
        }

        private SimpleFlowableList<FlowableList<SectionItem>> _items = new SimpleFlowableList<>();

        public <T> SectionedLayoutCreator addSection(int headerLayout, int layout, FlowableList<T> list) {
            final Section section = new Section(headerLayout, new BasicLayoutCreator(layout), list);

            _items.add(list.map(new Function<T, SectionItem>() {
                @Override
                public SectionItem<T> apply(T value) throws Exception {
                    return new SectionItem(section, value);
                }
            }));

            return this;
        }

        @Override
        public FlowableList<?> getList() {
            return FlowableList.concat(_items);
        }

        @Override
        public int getItemLayoutType(Object wrappedViewModel) {
            SectionItem sectionItem = (SectionItem) wrappedViewModel;

            Section owner = sectionItem.getOwner();
            Object viewModel = sectionItem.getValue();

            return owner.getLayout().getItemLayoutType(viewModel);
        }

        @Override
        public void bind(Object wrappedViewModel, RecyclerViewAdapter.ViewHolder holder) {
            SectionItem sectionItem = (SectionItem) wrappedViewModel;

            Object viewModel = sectionItem.getValue();

            super.bind(viewModel, holder);
        }

        @Override
        public Object createItemLayout(LayoutInflater inflater, ViewGroup parent, int layoutType) {
            View contentView = DataBindingUtil.inflate(inflater, layoutType, parent, false).getRoot();

            return new ItemViewHolder(contentView, layoutType);
        }
    }

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private List<?> _currentState;
        private Disposable _subscription;
        private ItemViewCreator _viewCreator;
        private ItemDataProvider _dataProvider;
        private MutableLifecycle _lifecycle;
        private RecyclerViewBindingSink _bindingSink;

        public interface ItemDataProvider {

            FlowableList<?> getList();
        }

        public static abstract class ItemViewCreator<TViewHolder> {

            class ItemViewHolder extends ViewHolder {

                final int layoutType;
                final CompositeDisposable disposable = new CompositeDisposable();

                public ItemViewHolder(View contentView, int layoutType) {
                    super(contentView);
                    this.layoutType = layoutType;
                }
            }

            public abstract int getItemLayoutType(Object viewModel);

            public abstract TViewHolder createItemLayout(LayoutInflater inflater, ViewGroup parent, int layoutType);

            public void bind(Object viewModel, ViewHolder holder) {
                holder.bind(viewModel, ((ItemViewHolder) holder).layoutType);
            }

            public void recycled(ViewHolder holder) {
                ((ItemViewHolder) holder).disposable.clear();
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }

            public View bind(Object viewModel, int layoutId) {
                // rebind the provided viewCoordinatorLayout
                ViewDataBinding binding = DataBindingUtil.getBinding(itemView);
                binding.setVariable(BR.model, viewModel);
                binding.executePendingBindings();

                return itemView;
            }
        }

        private LayoutInflater _cachedLayoutInflater = null;

        private LayoutInflater getCachedLayoutInflater(Context context) {
            if (_cachedLayoutInflater == null) {
                _cachedLayoutInflater = LayoutInflater.from(context);
            }
            return _cachedLayoutInflater;
        }

        private RecyclerViewAdapter(final FlowableList<?> list, ItemViewCreator viewCreator, @Nullable RecyclerViewBindingSink bindingSink) {
            this(new ItemDataProvider() {
                @Override
                public FlowableList<?> getList() {
                    return list;
                }
            }, viewCreator, bindingSink);
        }

        private RecyclerViewAdapter(ItemDataProvider dataProvider, ItemViewCreator viewCreator, @Nullable RecyclerViewBindingSink bindingSink) {
            _dataProvider = dataProvider;
            _viewCreator = viewCreator;

            _bindingSink = bindingSink;

            setHasStableIds(false);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);

            if (_bindingSink != null) {
                // If a RecyclerViewBindingSink is specified, it means that we expect this adapter
                // to stay bound forever (or until said sink decides to detach the adapter from the
                // recycler view)
                subscribe();
                _bindingSink.notifyRecyclerViewBound(recyclerView);
            } else {
                // Otherwise, it means that this adapter is expected to be "active" only when the
                // recycler is attached to the window.
                _lifecycle = new WindowAttachLifecycle(recyclerView);

                _lifecycle.addListener(new Lifecycle.Listener() {
                    @Override
                    public void onActive() {
                        subscribe();
                    }

                    @Override
                    public void onInactive() {
                        unsubscribe();
                    }
                });
            }
        }

        private void subscribe() {
            if (_subscription != null) {
                return;
            }

            FlowableList<?> list = _dataProvider.getList();

            if (list != null) {
                _subscription = list.updates()
                        .observeOn(UiThreadScheduler.uiThread())
                        .subscribe(new Consumer<Update<?>>() {
                            @Override
                            public void accept(Update<?> update) throws Exception {
                                _currentState = update.list;

                                for (int i = 0, l = update.changes.size(); i < l; ++i) {
                                    Change change = update.changes.get(i);

                                    switch (change.type) {
                                        case Moved:
                                            notifyItemMoved(change.from, change.to);
                                            break;

                                        case Inserted:
                                            notifyItemInserted(change.to);
                                            break;

                                        case Removed:
                                            notifyItemRangeRemoved(change.from, 1);
                                            break;

                                        case Reloaded:
                                            notifyDataSetChanged();
                                            break;
                                    }
                                }
                            }
                        });
            }
        }

        void unsubscribe() {
            if (_subscription == null) {
                return;
            }

            _subscription.dispose();
            _subscription = null;
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);

            if (_lifecycle != null) {
                _lifecycle.setActive(false);
            }
        }

        @Override
        public int getItemViewType(int position) {
            Object model = _currentState.get(position);

            return _viewCreator.getItemLayoutType(model);
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getCachedLayoutInflater(parent.getContext());

            return (ViewHolder) _viewCreator.createItemLayout(inflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
            Object model = _currentState.get(position);

            _viewCreator.bind(model, holder);
        }

        @Override
        public void onViewRecycled(@NonNull ViewHolder holder) {
            super.onViewRecycled(holder);

            _viewCreator.recycled(holder);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        public int getItemCount() {
            return _currentState == null ? 0 : _currentState.size();
        }
    }
}
