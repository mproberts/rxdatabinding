package com.github.mproberts.rxdatabinding.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.plugins.RxJavaPlugins;

public class UiThreadScheduler extends Scheduler {
    private static UiThreadScheduler _scheduler = new UiThreadScheduler();
    private final Handler _handler;

    private UiThreadScheduler() {
        _handler = new Handler(Looper.getMainLooper());
    }

    public static Scheduler uiThread() {
        return _scheduler;
    }

    private class ScheduledAction implements Disposable, Runnable {
        private Runnable _action;

        private ScheduledAction(Runnable action) {
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
                scheduledAction.run();

                return Disposables.disposed();
            }

            Message message = Message.obtain(_handler, scheduledAction);

            _handler.sendMessageDelayed(message, unit.toMillis(delay));

            if (_isDisposed) {
                _handler.removeCallbacks(scheduledAction);

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
