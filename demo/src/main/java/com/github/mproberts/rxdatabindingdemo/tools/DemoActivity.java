package com.github.mproberts.rxdatabindingdemo.tools;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mproberts.rxdatabindingdemo.R;
import com.github.mproberts.rxdatabindingdemo.di.DomainComponent;

public abstract class DemoActivity extends AppCompatActivity {

    protected DomainComponent getDomainComponent() {
        return ((ComponentProvider) getApplication()).getDomainComponent();
    }

    protected boolean showBackButton() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}
