package com.github.mproberts.rxdatabindingdemo.search.activity;

import com.github.mproberts.rxdatabindingdemo.R;
import com.github.mproberts.rxdatabindingdemo.di.AndroidBindingSearchActivity;
import com.github.mproberts.rxdatabindingdemo.search.SearchModule;
import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;

public class SearchActivity extends AndroidBindingSearchActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    public SearchViewModel navigateToSearch() {
        return getDomainComponent()
                .plus(new SearchModule())
                .viewModel();
    }
}
