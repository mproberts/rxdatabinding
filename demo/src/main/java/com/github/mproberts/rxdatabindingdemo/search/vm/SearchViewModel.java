package com.github.mproberts.rxdatabindingdemo.search.vm;

import com.github.mproberts.rxtools.list.FlowableList;

public interface SearchViewModel {
    interface Navigator {

        void navigateToSearch();
    }

    FlowableList<SearchListItemViewModel> searchList();

    void onQueryChanged(String query);
}
