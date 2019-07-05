package com.github.mproberts.rxdatabinding.tools;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import androidx.annotation.AttrRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.github.mproberts.rxdatabinding.internal.Lifecycle;
import com.github.mproberts.rxdatabinding.internal.MutableLifecycle;
import com.github.mproberts.rxdatabinding.internal.ViewBinding;
import com.github.mproberts.rxdatabinding.internal.WindowAttachLifecycle;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

@SuppressWarnings("unused")
public final class DataBindingTools {
    public static final int TAG_PROPERTY_MASK = "DataBindingTools.PROPERTY".hashCode();
    public static final int TAG_LIFECYCLE = "DataBindingTools.LIFECYCLE".hashCode();
    private static Consumer<Throwable> _exceptionHandler;

    private DataBindingTools() {
    }

    public static <T> void bindViewProperty(
            @AttrRes int boundId,
            Consumer<T> binder,
            View view,
            Flowable<T> observable) {
        bindViewProperty(boundId, binder, view, observable, null, null);
    }

    public static void watchActivityAttachment(final View view, final Consumer<Activity> activityWatcher) {
        WindowAttachLifecycle lifecycle = new WindowAttachLifecycle(view);

        lifecycle.addListener(new Lifecycle.Listener() {
            @Override
            public void onActive() {
                Context context = view.getContext();

                while (context != null && context instanceof ContextWrapper) {
                    if (context instanceof Activity) {
                        break;
                    }

                    context = ((ContextWrapper) context).getBaseContext();
                }

                if (context == null) {
                    return;
                }

                try {
                    activityWatcher.accept((Activity) context);
                } catch (Exception e) {
                    handleError(e);
                }
            }

            @Override
            public void onInactive() {
                try {
                    activityWatcher.accept(null);
                } catch (Exception e) {
                    handleError(e);
                }
            }
        });
    }

    public static <T> void bindViewProperty(@AttrRes int boundId,
                                            Consumer<T> binder,
                                            View view,
                                            Flowable<T> observable,
                                            T defaultValue,
                                            Action reset) {
        bindViewProperty(boundId, binder, view, observable, defaultValue, reset, true);
    }

    public static <T> void bindViewProperty(@AttrRes int boundId,
                                            Consumer<T> binder,
                                            View view,
                                            Flowable<T> observable,
                                            T defaultValue,
                                            Action reset,
                                            boolean onMain) {
        int bindingTagKey = TAG_PROPERTY_MASK ^ boundId;
        Object previousTag = view.getTag(bindingTagKey);

        if (previousTag != null && previousTag instanceof ViewBinding) {
            ViewBinding previousBinding = (ViewBinding) previousTag;

            previousBinding.detach();
        }

        if (observable != null) {
            MutableLifecycle lifecycle = setupLifecycle(view);

            lifecycle.setActive(true);

            ViewBinding<T> binding = new ViewBinding<>(binder, observable, reset, lifecycle, onMain);

            view.setTag(bindingTagKey, binding);
        } else {
            view.setTag(bindingTagKey, null);

            try {
                binder.accept(defaultValue);
            } catch (Exception e) {
                handleError(e);
            }
        }
    }

    public static void bindOnlyOnce(View view, int boundId, Runnable action) {
        if (view.getTag(boundId) != null) {
            return;
        }

        view.setTag(boundId, new Object());
        action.run();
    }

    private static MutableLifecycle setupLifecycle(View view) {
        MutableLifecycle lifecycle = (MutableLifecycle) view.getTag(TAG_LIFECYCLE);

        if (lifecycle == null) {
            lifecycle = new WindowAttachLifecycle(view);
        }

        view.setTag(TAG_LIFECYCLE, lifecycle);

        return lifecycle;
    }

    public static void setExceptionHandler(Consumer<Throwable> handler) {
        _exceptionHandler = handler;
    }

    public static void handleError(Throwable e) {
        Consumer<Throwable> exceptionHandler = _exceptionHandler;

        if (exceptionHandler != null) {
            try {
                exceptionHandler.accept(e);
            } catch (Exception fatalError) {
                throw new RuntimeException(fatalError);
            }
        }
    }
}
