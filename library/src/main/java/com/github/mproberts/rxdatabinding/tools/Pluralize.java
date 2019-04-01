package com.github.mproberts.rxdatabinding.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import android.os.Build;
import androidx.annotation.StringRes;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.list.Update;
import com.github.mproberts.rxtools.types.Optional;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class Pluralize {

    private static final String ENGLISH_LOCALE_LANGUAGE = new Locale("en").getLanguage();
    private static final String ARABIC_LOCALE_LANGUAGE = new Locale("ar").getLanguage();
    private static final String CHINESE_LOCALE_LANGUAGE = new Locale("zh-Hans").getLanguage();
    private static final String CHINESE_TRADITIONAL_LOCALE_LANGUAGE = new Locale("zh-Hant").getLanguage();
    private static final String JAPANESE_LOCALE_LANGUAGE = new Locale("ja").getLanguage();
    private static final String KOREAN_LOCALE_LANGUAGE = new Locale("ko").getLanguage();

    private Pluralize() {
        // intentionally blank
    }

    public static class CommaSplit {

        final List<String> values;
        @StringRes final int zero;
        @StringRes final int single;
        @StringRes final int any;

        @Nullable final String zeroString;
        @Nullable final String singleString;
        @Nullable final String anyString;

        public CommaSplit(List<String> values, int zero, int single, int any) {
            this.values = values;

            this.zero = zero;
            this.single = single;
            this.any = any;

            this.zeroString = null;
            this.singleString = null;
            this.anyString = null;
        }

        public CommaSplit(List<String> values, String zeroString, String singleString, String anyString) {
            this.values = values;

            this.zero = -1;
            this.single = -1;
            this.any = -1;

            this.zeroString = zeroString;
            this.singleString = singleString;
            this.anyString = anyString;
        }

        @SuppressLint("ResourceType")
        public String toCommaSeparatedString(Context context) {
            Resources resources = context.getResources();
            String separator = ", ";

            Locale locale;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = context.getResources().getConfiguration().getLocales().get(0);
            } else {
                //noinspection deprecation
                locale = context.getResources().getConfiguration().locale;
            }

            String displayLanguage = locale.getDisplayLanguage();

            if (displayLanguage.equals(ENGLISH_LOCALE_LANGUAGE)) {
                // skip
            } else if (displayLanguage.equals(ARABIC_LOCALE_LANGUAGE)) {
                separator = "\u060C";
            } else if (displayLanguage.equals(CHINESE_LOCALE_LANGUAGE)
                    || displayLanguage.equals(CHINESE_TRADITIONAL_LOCALE_LANGUAGE)
                    || displayLanguage.equals(JAPANESE_LOCALE_LANGUAGE)
                    || displayLanguage.equals(KOREAN_LOCALE_LANGUAGE)) {
                separator = "\u3001";
            }

            if (values == null || values.size() == 0) {
                if (zero > 0) {
                    return resources.getString(zero);
                } else if (zeroString != null) {
                    return zeroString;
                }
            } else if (values.size() == 1) {
                if (single > 0) {
                    return resources.getString(single, values.get(0));
                } else if (singleString != null) {
                    return String.format(singleString, values.get(0));
                }
            } else {
                String valueString = values.get(0);
                String lastString = values.get(values.size() - 1);

                for (int i = 1, l = values.size() - 1; i < l; ++i) {
                    valueString += separator + values.get(i);
                }

                if (any > 0) {
                    return resources.getString(any, valueString, lastString);
                } else if (anyString != null) {
                    return String.format(anyString, valueString, lastString);
                }
            }

            return "";
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }
    }

    @BindingAdapter("android:text")
    public static void bindCommaSplitText(final TextView view, CommaSplit split) {
        split.toCommaSeparatedString(view.getContext());
    }

    @BindingAdapter("android:text")
    public static void bindCommaSplitFlowable(final TextView view, Flowable<CommaSplit> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.text, new Consumer<CommaSplit>() {
            @Override
            public void accept(CommaSplit split) throws Exception {
                view.setText(split.toCommaSeparatedString(view.getContext()));
            }
        }, view, newValue);
    }

    @BindingAdapter("android:text")
    public static void bindCommaSplitText(final TextSwitcher view, CommaSplit split) {
        split.toCommaSeparatedString(view.getContext());
    }

    @BindingAdapter("android:text")
    public static void bindCommaSplitFlowable(final TextSwitcher view, Flowable<CommaSplit> newValue) {
        DataBindingTools.bindViewProperty(android.R.attr.text, new Consumer<CommaSplit>() {
            @Override
            public void accept(CommaSplit split) throws Exception {
                view.setText(split.toCommaSeparatedString(view.getContext()));
            }
        }, view, newValue);
    }

    public static CommaSplit list(List<String> values, @StringRes int zero, @StringRes int single, @StringRes int any) {
        return new CommaSplit(values, zero, single, any);
    }

    public static Flowable<CommaSplit> list(Flowable<List<String>> values, @StringRes final int zero, @StringRes final int single, @StringRes final int any) {
        return values.map(new Function<List<String>, CommaSplit>() {
            @Override
            public CommaSplit apply(@NonNull List<String> values) throws Exception {
                return new CommaSplit(values, zero, single, any);
            }
        });
    }

    public static Flowable<CommaSplit> list(FlowableList<String> values, @StringRes final int zero, @StringRes final int single, @StringRes final int any) {
        return values.updates().map(new Function<Update<String>, CommaSplit>() {
            @Override
            public CommaSplit apply(@NonNull Update<String> update) throws Exception {
                return new CommaSplit(update.list, zero, single, any);
            }
        });
    }

    public static Flowable<CommaSplit> list(FlowableList<String> values, final String zero, final String single, final String any) {
        return values.updates().map(new Function<Update<String>, CommaSplit>() {
            @Override
            public CommaSplit apply(@NonNull Update<String> update) throws Exception {
                return new CommaSplit(update.list, zero, single, any);
            }
        });
    }

    public static Flowable<CommaSplit> optionalList(FlowableList<Optional<String>> values, final String zero, final String single, final String any, final String empty) {
        return values.updates().map(new Function<Update<Optional<String>>, CommaSplit>() {
            @Override
            public CommaSplit apply(@NonNull Update<Optional<String>> update) throws Exception {
                List<Optional<String>> list = update.list;
                ArrayList<String> nonOptional = new ArrayList<>(list.size());

                for (Optional<String> rawString : list) {
                    nonOptional.add(rawString.orElse(empty));
                }


                return new CommaSplit(nonOptional, zero, single, any);
            }
        });
    }

    public static Flowable<CommaSplit> flowableList(Flowable<List<Flowable<String>>> values, @StringRes final int zero, @StringRes final int single, @StringRes final int any) {

        return values
                .flatMap(new Function<List<Flowable<String>>, Publisher<List<String>>>() {
                    @Override
                    public Publisher<List<String>> apply(@NonNull List<Flowable<String>> flowables) throws Exception {
                        if (flowables.size() == 0) {
                            return Flowable.<List<String>>just(new ArrayList<String>());
                        }

                        return Flowable.combineLatest(flowables, new Function<Object[], List<String>>() {
                            @Override
                            public List<String> apply(@NonNull Object[] objects) throws Exception {
                                @SuppressWarnings("unchecked")
                                List<String> strings = (List<String>) (Object) Arrays.asList(objects);

                                return strings;
                            }
                        });
                    }
                })
                .map(new Function<List<String>, CommaSplit>() {
                    @Override
                    public CommaSplit apply(@NonNull List<String> strings) throws Exception {
                        return new CommaSplit(strings, zero, single, any);
                    }
                });
    }

    public static Flowable<CommaSplit> flowableList(Flowable<List<Flowable<String>>> values, @StringRes int single, @StringRes int any) {
        return flowableList(values, -1, single, any);
    }

    public static CommaSplit list(List<String> values, @StringRes int single, @StringRes int any) {
        return list(values, -1, single, any);
    }

    public static Flowable<CommaSplit> list(Flowable<List<String>> values, @StringRes final int single, @StringRes final int any) {
        return list(values, -1, single, any);
    }
}
