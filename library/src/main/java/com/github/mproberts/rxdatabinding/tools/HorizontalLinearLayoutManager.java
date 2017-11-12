package com.github.mproberts.rxdatabinding.tools;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class HorizontalLinearLayoutManager extends LinearLayoutManager {

    public HorizontalLinearLayoutManager(Context context) {
        super(context);
        init();
    }

    public HorizontalLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    public HorizontalLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }
}
