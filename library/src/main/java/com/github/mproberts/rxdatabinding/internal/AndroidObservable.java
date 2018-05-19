package com.github.mproberts.rxdatabinding.internal;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.github.mproberts.rxdatabinding.tools.UiThreadScheduler;

import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

class AndroidObservable {

    private static class ConstantCallable<T> implements Callable<Flowable<T>> {
        private final Flowable<T> flowable;

        public ConstantCallable(Flowable<T> flowable) {
            this.flowable = flowable;
        }

        @Override
        public Flowable<T> call() throws Exception {
            return flowable;
        }
    }

    static class SimpleObservableField<T> extends ObservableField<T> {

        private Flowable<T> flowable;
        private final Callable<Flowable<T>> flowableCreator;
        private Disposable disposable;
        private int callbackCounter;

        public SimpleObservableField(Callable<Flowable<T>> flowableCreator) {
            this.flowableCreator = flowableCreator;
        }

        @Override
        public void addOnPropertyChangedCallback(@NonNull OnPropertyChangedCallback callback) {
            super.addOnPropertyChangedCallback(callback);

            if (++callbackCounter == 1) {
                try {
                    flowable = flowableCreator.call();
                } catch (Exception e) {
                    try {
                        RxJavaPlugins.getErrorHandler().accept(e);
                    } catch (Exception e1) {
                        // ignored
                    }
                }

                // bind observable
                disposable = flowable
                        .observeOn(UiThreadScheduler.uiThread())
                        .subscribe(new Consumer<T>() {
                            @Override
                            public void accept(T t) throws Exception {
                                internalSet(t);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                RxJavaPlugins.getErrorHandler().accept(throwable);
                            }
                        });
            }
        }

        @Override
        public void removeOnPropertyChangedCallback(@NonNull OnPropertyChangedCallback callback) {
            super.removeOnPropertyChangedCallback(callback);

            if (--callbackCounter == 0) {
                // dispose
                Disposable localDisposable = disposable;

                disposable = null;

                localDisposable.dispose();
            }
        }

        @Override
        public void set(T value) {
            throw new UnsupportedOperationException("Observables cannot be mutated");
        }

        void internalSet(T value) {
            super.set(value);
        }
    }

    public static <T> ObservableField<T> convert(final Flowable<T> flowable) {
        return convert(new ConstantCallable<>(flowable));
    }

    public static <T> ObservableField<T> convert(Callable<Flowable<T>> flowableCreator) {
        return new SimpleObservableField<>(flowableCreator);
    }
}
