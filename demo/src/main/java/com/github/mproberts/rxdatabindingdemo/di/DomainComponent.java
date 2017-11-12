package com.github.mproberts.rxdatabindingdemo.di;

import com.github.mproberts.rxdatabindingdemo.home.HomeComponent;
import com.github.mproberts.rxdatabindingdemo.home.HomeModule;
import com.github.mproberts.rxdatabindingdemo.profile.ProfileComponent;
import com.github.mproberts.rxdatabindingdemo.profile.ProfileModule;
import com.github.mproberts.rxdatabindingdemo.search.SearchComponent;
import com.github.mproberts.rxdatabindingdemo.search.SearchModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        DomainModule.class,
        NavigationModule.class,
        RoomModule.class
})
public interface DomainComponent {

    HomeComponent plus(HomeModule module);

    ProfileComponent plus(ProfileModule module);

    SearchComponent plus(SearchModule module);
}
