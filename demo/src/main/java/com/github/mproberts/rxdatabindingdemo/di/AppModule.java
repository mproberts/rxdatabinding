package com.github.mproberts.rxdatabindingdemo.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application _application;

    public AppModule(Application application) {
        _application = application;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return _application;
    }
}
