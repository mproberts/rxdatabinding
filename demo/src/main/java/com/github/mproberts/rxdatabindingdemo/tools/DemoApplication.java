package com.github.mproberts.rxdatabindingdemo.tools;

import android.app.Application;

import com.github.mproberts.rxdatabindingdemo.di.AppModule;
import com.github.mproberts.rxdatabindingdemo.di.DaggerDomainComponent;
import com.github.mproberts.rxdatabindingdemo.di.DomainComponent;

public class DemoApplication extends Application implements ComponentProvider {

    private DomainComponent _domainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        _domainComponent = DaggerDomainComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public DomainComponent getDomainComponent() {
        return _domainComponent;
    }
}
