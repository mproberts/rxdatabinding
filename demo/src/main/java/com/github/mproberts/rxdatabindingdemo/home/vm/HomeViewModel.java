package com.github.mproberts.rxdatabindingdemo.home.vm;

import com.github.mproberts.rxtools.list.FlowableList;

public interface HomeViewModel {
    FlowableList<ContactListItemViewModel> contactList();

    void onSearchTapped();
}
