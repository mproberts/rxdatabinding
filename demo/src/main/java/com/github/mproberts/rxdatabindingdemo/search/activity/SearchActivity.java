package com.github.mproberts.rxdatabindingdemo.search.activity;

import android.content.Intent;

import com.github.mproberts.rxdatabindingdemo.R;
import com.github.mproberts.rxdatabindingdemo.di.DomainComponent;
import com.github.mproberts.rxdatabindingdemo.search.SearchModule;
import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;
import com.github.mproberts.rxdatabindingdemo.tools.DemoActivity;

public class SearchActivity extends DemoActivity<SearchViewModel> {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    protected SearchViewModel createViewModel(Intent intent, DomainComponent domainComponent) {
        return domainComponent.plus(new SearchModule()).viewModel();
    }
}
