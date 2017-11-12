package com.github.mproberts.rxdatabinding.bindings;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mproberts.rxdatabinding.BR;
import com.github.mproberts.rxdatabinding.tools.DataBindingTools;
import com.github.mproberts.rxdatabinding.tools.UiThreadScheduler;
import com.github.mproberts.rxtools.list.Change;
import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.list.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public final class RecyclerViewDataBindings {
    private RecyclerViewDataBindings() {
    }

    interface LayoutManagerCreator extends Callable<RecyclerView.LayoutManager> {
    }

    @BindingAdapter(value = {"data", "itemLayout"})
    public static void bindList(RecyclerView recyclerView, FlowableList<?> list, @LayoutRes int layoutId) {
        recyclerView.setAdapter(new RecyclerViewAdapter(list, new BasicLayoutCreator(layoutId)));
    }

    @BindingAdapter(value = {"data", "itemLayoutCreator"})
    public static void bindList(RecyclerView recyclerView, FlowableList<?> list, RecyclerViewAdapter.ItemViewCreator layoutCreator) {
        recyclerView.setAdapter(new RecyclerViewAdapter(list, layoutCreator));
    }

    @BindingAdapter(value = {"layoutManager"})
    public static void bindListLayoutManager(RecyclerView recyclerView, LayoutManagerCreator managerCreator) {
        try {
            recyclerView.setLayoutManager(managerCreator.call());
        } catch (Exception e) {
            DataBindingTools.handleError(e);
        }
    }

    public static class BasicLayoutCreator implements RecyclerViewAdapter.ItemViewCreator {
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

            return new ItemViewHolder(contentView);
        }
    }

    public static abstract class PredicateLayoutCreator<T> implements RecyclerViewAdapter.ItemViewCreator {
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

            return new ItemViewHolder(contentView);
        }
    }

    public static class TypedLayoutCreator implements RecyclerViewAdapter.ItemViewCreator {
        private Map<Class<?>, Integer> _layouts = new HashMap<>();

        public TypedLayoutCreator addLayout(@LayoutRes int layoutId, Class<?> clazz) {
            _layouts.put(clazz, layoutId);

            return this;
        }

        @Override
        public final int getItemLayoutType(Object viewModel) {
            return _layouts.get(viewModel.getClass());
        }

        @Override
        public final Object createItemLayout(LayoutInflater inflater, ViewGroup parent, int layoutType) {
            View contentView = DataBindingUtil.inflate(inflater, layoutType, parent, false).getRoot();

            return new ItemViewHolder(contentView);
        }
    }

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private FlowableList<?> _list;
        private List<?> _currentState;
        private Disposable _subscription;
        private ItemViewCreator _viewCreator;

        public interface ItemViewCreator<TViewHolder> {
            class ItemViewHolder extends ViewHolder {

                public ItemViewHolder(View contentView) {
                    super(contentView);
                }
            }

            int getItemLayoutType(Object viewModel);

            TViewHolder createItemLayout(LayoutInflater inflater, ViewGroup parent, int layoutType);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
            }

            public View bind(Object viewModel) {
                // rebind the provided view
                ViewDataBinding binding = DataBindingUtil.getBinding(itemView);
                binding.setVariable(BR.model, viewModel);
                binding.executePendingBindings();

                return itemView;
            }
        }

        private RecyclerViewAdapter(FlowableList<?> list, ItemViewCreator viewCreator) {
            _list = list;
            _viewCreator = viewCreator;

            setHasStableIds(false);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);

            if (_list != null) {
                _subscription = _list.updates()
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

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);

            if (_subscription != null) {
                _subscription.dispose();
            }
        }

        @Override
        public int getItemViewType(int position) {
            Object model = _currentState.get(position);

            return _viewCreator.getItemLayoutType(model);
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            return (ViewHolder) _viewCreator.createItemLayout(inflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
            Object model = _currentState.get(position);

            holder.bind(model);
        }

        @Override
        public int getItemCount() {
            return _currentState == null ? 0 : _currentState.size();
        }
    }
}
