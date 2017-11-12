package com.github.mproberts.rxdatabinding.tools;

import android.support.annotation.Dimension;

import com.github.mproberts.rxtools.types.Optional;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public final class Binding {

    private Binding() {
        // intentionally blank
    }

    public static Flowable<Boolean> delayFalse(Flowable<Boolean> flowable, final long duration) {

        return flowable.switchMap(new Function<Boolean, Publisher<Boolean>>() {
            @Override
            public Publisher<Boolean> apply(@NonNull Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    return Flowable.just(true);
                }

                return Flowable.timer(duration, TimeUnit.MILLISECONDS)
                        .map(new Function<Long, Boolean>() {
                            @Override
                            public Boolean apply(@NonNull Long aLong) throws Exception {
                                return false;
                            }
                        });
            }
        });
    }

    public static Flowable<Integer> conditionalInt(final Flowable<Boolean> flowable, final int ifTrue, final int ifFalse) {
        return flowable.map(new Function<Boolean, Integer>() {
            @Override
            public Integer apply(@NonNull Boolean aBoolean) throws Exception {
                return aBoolean == Boolean.TRUE ? ifTrue : ifFalse;
            }
        });
    }

    public static Flowable<Float> optionalDimension(final Flowable<Boolean> flowable, @Dimension final float dimension) {
        return flowable.map(new Function<Boolean, Float>() {
            @Override
            public Float apply(@NonNull Boolean aBoolean) throws Exception {
                return aBoolean == Boolean.TRUE ? dimension : 0.f;
            }
        });
    }

    public static Flowable<Boolean> asBoolean(Flowable<Optional<String>> flowable) {

        return flowable.map(new Function<Optional<String>, Boolean>() {
            @Override
            public Boolean apply(@NonNull Optional<String> optional) throws Exception {
                return optional.isPresent();
            }
        });
    }

    public static Flowable<Boolean> invertOptional(Flowable<Optional<?>> flowable) {

        return flowable.map(new Function<Optional<?>, Boolean>() {
            @Override
            public Boolean apply(@NonNull Optional<?> optional) throws Exception {
                return !optional.isPresent();
            }
        });
    }

    public static Flowable<Boolean> invert(Flowable<Boolean> flowable) {

        return flowable.map(new Function<Boolean, Boolean>() {
            @Override
            public Boolean apply(@NonNull Boolean value) throws Exception {
                return value == Boolean.FALSE;
            }
        });
    }
}
