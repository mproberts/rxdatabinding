package com.github.mproberts.rxdatabinding.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;

import com.github.mproberts.rxtools.list.Change;
import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.list.Update;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class NotificationBindingHandler<T> extends BroadcastReceiver {

    private static final int INVALID_NOTIFICATION_ID = 0;

    private static final String KEY_NOTIFICATION_ID = "com.kik.kikx.NOTIFICATION_ID";
    private static final String KEY_MAPPED_ACTION = "com.kik.kikx.MAPPED_ACTION";

    public static final class NotificationBinding implements Disposable {

        private final CompositeDisposable _localDisposable = new CompositeDisposable();

        private NotificationBinding() {
        }

        @Override
        public void dispose() {
            _localDisposable.dispose();
        }

        @Override
        public boolean isDisposed() {
            return _localDisposable.isDisposed();
        }
    }

    public interface NotificationCreator<T> {
        int createNotification(T model, NotificationBinding binding);
    }

    private String _channelId;
    private final NotificationManagerCompat _notificationManager;

    private CompositeDisposable _listSubscription = new CompositeDisposable();
    private FlowableList<T> _boundList = null;
    private List<NotificationBinding> _notificationBindings = new ArrayList<>();

    public NotificationBindingHandler(Context context) {
        _notificationManager = NotificationManagerCompat.from(context);

        setupNotificationChannel(context);

        // TODO: listen to something
    }

    public void setList(FlowableList<T> list) {
        FlowableList<T> boundList = _boundList;

        _listSubscription.dispose();
        _boundList = list;

        if (list != null) {

            list.updates().subscribe(new Subscriber<Update<T>>() {
                @Override
                public void onSubscribe(Subscription s) {

                }

                @Override
                public void onNext(Update<T> update) {
                    for (Change change : update.changes) {
                        switch (change.type) {
                            case Inserted: {
                                NotificationBinding binding = bindItem(update.list.get(change.to));
                                _notificationBindings.add(change.to, binding);
                                break;
                            }
                            case Moved: {
                                NotificationBinding moved = _notificationBindings.remove(change.from);
                                _notificationBindings.add(change.to, moved);
                                break;
                            }
                            case Reloaded: {
                                // TODO: cancel notifications
                                List<NotificationBinding> notificationBindings = _notificationBindings;

                                List<NotificationBinding> reloadedNotificationBindings = new ArrayList<>();

                                for (NotificationBinding binding : notificationBindings) {
                                    binding.dispose();
                                }

                                for (T model : update.list) {
                                    NotificationBinding binding = bindItem(model);

                                    reloadedNotificationBindings.add(binding);
                                }

                                _notificationBindings = reloadedNotificationBindings;

                                break;
                            }
                            case Removed: {
                                NotificationBinding removed = _notificationBindings.remove(change.from);

                                removed.dispose();
                                break;
                            }
                        }
                    }

                }

                @Override
                public void onError(Throwable t) {
                }

                @Override
                public void onComplete() {
                }
            });
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private NotificationChannel createNotificationChannel(String channelId, String name, int importance, String description) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId, name, importance);

        notificationChannel.setDescription(description);

        return notificationChannel;
    }

    private void setupNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationService = context.getSystemService(NotificationManager.class);

            if (notificationService != null) {
                NotificationChannel channel = createNotificationChannel(_channelId, "", NotificationManager.IMPORTANCE_DEFAULT, "");
                notificationService.createNotificationChannel(channel);
            }
        }
    }

    private NotificationBinding bindItem(T model) {
        return new NotificationBinding();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }
}
