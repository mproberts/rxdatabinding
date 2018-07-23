package com.github.mproberts.rxdatabindingdemo.home.vm;

import io.reactivex.Flowable;

public class NotificationViewModel {

    private final int _notificationId;
    private final String _title;
    private final String _subtitle;

    public NotificationViewModel(int notificationId, String title, String subtitle) {
        _notificationId = notificationId;
        _title = title;
        _subtitle = subtitle;
    }

    public int notificationId() {
        return _notificationId;
    }

    public Flowable<String> title() {
        return Flowable.just(_title);
    }

    public Flowable<String> subtitle() {
        return Flowable.just(_subtitle);
    }

    public void onItemTapped() {
    }
}
