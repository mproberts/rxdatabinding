package com.github.mproberts.rxdatabindingdemo.search;

import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;

import dagger.Subcomponent;

@Subcomponent(modules = {SearchModule.class})
public interface SearchComponent {

    SearchViewModel viewModel();
}
