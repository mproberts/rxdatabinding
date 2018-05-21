package com.github.mproberts.rxdatabinding.internal;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.github.mproberts.rxdatabinding.tools.UiThreadScheduler;
import com.github.mproberts.rxtools.types.Optional;

import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class AndroidObservable {

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

    static class SimpleObservableBoolean extends ObservableBoolean {

        private Flowable<Boolean> flowable;
        private final Callable<Flowable<Boolean>> flowableCreator;
        private Disposable disposable;
        private int callbackCounter;

        public SimpleObservableBoolean(Callable<Flowable<Boolean>> flowableCreator) {
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
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean t) throws Exception {
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
        public void set(boolean value) {
            throw new UnsupportedOperationException("Observables cannot be mutated");
        }

        void internalSet(boolean value) {
            super.set(value);
        }
    }

    static class UnwrappedOptionalObservableField<T> extends ObservableField<T> {
        private Flowable<Optional<T>> flowable;
        private final Callable<Flowable<Optional<T>>> flowableCreator;
        private Disposable disposable;
        private int callbackCounter;

        public UnwrappedOptionalObservableField(Callable<Flowable<Optional<T>>> flowableCreator) {
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
                        .subscribe(new Consumer<Optional<T>>() {
                            @Override
                            public void accept(Optional<T> t) throws Exception {
                                internalSet(t.orElse(null));
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

    public static ObservableBoolean convertBoolean(final Flowable<Boolean> flowable) {
        return convertBoolean(new ConstantCallable<>(flowable));
    }

    public static ObservableBoolean convertBoolean(Callable<Flowable<Boolean>> flowableCreator) {
        return new SimpleObservableBoolean(flowableCreator);
    }

    public static <T> ObservableField<T> convertOptional(final Flowable<Optional<T>> flowable) {
        return convertOptional(new ConstantCallable<>(flowable));
    }

    public static <T> ObservableField<T> convertOptional(Callable<Flowable<Optional<T>>> flowableCreator) {
        return new UnwrappedOptionalObservableField<>(flowableCreator);
    }
}
