package com.github.mproberts.rxdatabindingdemo.tools;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mproberts.rxdatabindingdemo.BR;
import com.github.mproberts.rxdatabindingdemo.R;
import com.github.mproberts.rxdatabindingdemo.di.DomainComponent;

public abstract class DemoActivity<T> extends AppCompatActivity {

    private ViewDataBinding _binding;
    private Intent _intent;

    protected DomainComponent getDomainComponent() {
        return ((ComponentProvider) getApplication()).getDomainComponent();
    }

    protected abstract
    @LayoutRes
    int getLayoutResource();

    protected boolean showBackButton() {
        return true;
    }

    protected abstract T createViewModel(Intent intent, DomainComponent domainComponent);

    @SuppressWarnings("unchecked cast")
    protected <T> T getArgument(int position, Class<T> clazz) {

        Intent intent = _intent;
        String extraName = "arg" + position;
        Object result;

        if (clazz.equals(String.class)) {
            return (T) intent.getStringExtra(extraName);
        } else if (clazz.equals(Integer.class)) {
            result = intent.getIntExtra(extraName, 0);
        } else if (clazz.equals(Float.class)) {
            result = intent.getFloatExtra(extraName, 0.f);
        } else if (clazz.equals(Double.class)) {
            result = intent.getDoubleExtra(extraName, 0.f);
        } else {
            result = intent.getSerializableExtra(extraName);
        }

        return (T) result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _binding = DataBindingUtil.setContentView(this, getLayoutResource());
        _intent = getIntent();

        T viewModel = createViewModel(_intent, getDomainComponent());

        _binding.setVariable(BR.model, viewModel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener((view) -> finish());
        }

        if (actionBar != null) {
            if (showBackButton()) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        _intent = intent;

        T viewModel = createViewModel(_intent, getDomainComponent());

        _binding.setVariable(BR.model, viewModel);
    }
}
