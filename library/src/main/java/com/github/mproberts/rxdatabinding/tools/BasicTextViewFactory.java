package com.github.mproberts.rxdatabinding.tools;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.github.mproberts.rxdatabinding.bindings.SwitcherDataBindings;

public class BasicTextViewFactory implements SwitcherDataBindings.ViewFactoryCreator {

    public static final SwitcherDataBindings.ViewFactoryCreator create() {
        return new BasicTextViewFactory();
    }

    @Override
    public ViewSwitcher.ViewFactory getFactory(final Context context) {
        return new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(context);
            }
        };
    }
}
