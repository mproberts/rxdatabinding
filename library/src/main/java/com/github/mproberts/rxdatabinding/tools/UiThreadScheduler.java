package com.github.mproberts.rxdatabinding.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.plugins.RxJavaPlugins;

public class UiThreadScheduler extends Scheduler {
    private static UiThreadScheduler _scheduler = new UiThreadScheduler();
    private final Handler _handler;
    private List<ScheduledAction> _queuedActions = new ArrayList<>();
    private List<ScheduledAction> _swapActions = new ArrayList<>();
    private final FlushAction _flushAction = new FlushAction();
    private boolean _isScheduled = false;
    private final Object _queueLock = new Object();

    private AtomicInteger _incrementer = new AtomicInteger(0);

    private UiThreadScheduler() {
        _handler = new Handler(Looper.getMainLooper());
    }

    public static Scheduler uiThread() {
        return _scheduler;
    }

    private class ScheduledAction implements Disposable, Runnable {
        final int _id;
        private Runnable _action;

        private ScheduledAction(Runnable action) {
            _id = _incrementer.incrementAndGet();
            _action = action;
        }

        @Override
        public void dispose() {
            _action = null;
        }

        @Override
        public boolean isDisposed() {
            return _action == null;
        }

        @Override
        public void run() {
            Runnable action = _action;

            if (action != null) {
                try {
                    action.run();
                } catch (Throwable t) {
                    try {
                        RxJavaPlugins.getErrorHandler().accept(t);
                    } catch (Exception e) {
                        // error of last resort
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private class FlushAction implements Runnable {

        @Override
        public void run() {
            flush();
        }
    }

    private void flush() {
        List<ScheduledAction> actions;

        synchronized (_queueLock) {
            _isScheduled = false;

            actions = _queuedActions;

            if (_swapActions != null) {
                _queuedActions = _swapActions;
                _swapActions = null;
            } else {
                _queuedActions = new ArrayList<>();
            }
        }

        for (int i = 0, c = actions.size(); i < c; ++i) {
            ScheduledAction action = actions.get(i);

            action.run();
        }

        synchronized (_queueLock) {
            if (_swapActions == null) {
                actions.clear();

                _swapActions = actions;
            }
        }
    }

    class UiThreadWorker extends Worker {
        private volatile boolean _isDisposed;
        private final Handler _handler;

        UiThreadWorker(Handler handler) {
            _handler = handler;
        }

        @Override
        public Disposable schedule(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
            if (_isDisposed) {
                return Disposables.disposed();
            }

            ScheduledAction scheduledAction = new ScheduledAction(run);

            if (Looper.myLooper() == Looper.getMainLooper()) {
                flush();

                scheduledAction.run();

                return Disposables.disposed();
            }

            if (delay > 0) {
                _handler.postDelayed(scheduledAction, unit.toMillis(delay));
            } else {
                synchronized (_queueLock) {
                    _queuedActions.add(scheduledAction);

                    if (!_isScheduled) {
                        _isScheduled = true;

                        _handler.post(_flushAction);
                    }
                }
            }

            if (_isDisposed) {
                scheduledAction.dispose();

                return Disposables.disposed();
            }

            return scheduledAction;
        }

        @Override
        public void dispose() {
            _isDisposed = true;
        }

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }
    }

    @Override
    public Worker createWorker() {
        return new UiThreadWorker(_handler);
    }
}
