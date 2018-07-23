package com.github.mproberts.rxdatabinding.notifications;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.github.mproberts.rxtools.list.Change;
import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.list.Update;
import com.github.mproberts.rxtools.types.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class NotificationBindingHandler<T> extends BroadcastReceiver {

    private static final int INVALID_NOTIFICATION_ID = 0;

    private static final String KEY_NOTIFICATION_ID = "com.github.mproberts.rxdatabinding.NOTIFICATION_ID";
    private static final String KEY_MAPPED_ACTION = "com.github.mproberts.rxdatabinding.MAPPED_ACTION";
    private static final String TAP_ACTION_NAME = "com.github.mproberts.rxdatabinding.TAP_ACTION";

    public interface NotificationBinding {

        void setContentText(Flowable<String> text);

        void setContentTitle(Flowable<String> title);

        void setTapActionListener(Class<? extends Activity> activity, Action action);

        void setOnDismissListener(Action dismissAction);

        void addActionListener(Class<? extends Activity> activity, @DrawableRes int icon, String title, Action action);

        <U> void bind(Flowable<Optional<U>> source, final U ifEmpty, final Consumer<U> binding);

        <U> void bind(Flowable<U> source, final Consumer<U> binding);

        NotificationCompat.Builder getBuilder();

        void setNotificationId(int notificationId);
    }

    public final class BaseNotificationBinding implements NotificationBinding, Disposable {

        private final CompositeDisposable _localDisposable = new CompositeDisposable();
        private int _notificationId = INVALID_NOTIFICATION_ID;
        private final NotificationCompat.Builder _builder = new NotificationCompat.Builder(getContext(), getChannelId());
        private final Map<String, Action> _mappedActions = new HashMap<>();
        private Action _dismissAction;
        private boolean _isReady = false;

        @Override
        public void setContentTitle(Flowable<String> title) {
            bind(title, new Consumer<String>() {
                @Override
                public void accept(String title) throws Exception {
                    _builder.setContentTitle(title);
                }
            });
        }

        @Override
        public void setContentText(Flowable<String> text) {
            bind(text, new Consumer<String>() {
                @Override
                public void accept(String text) throws Exception {
                    _builder.setContentText(text);
                }
            });
        }

        @Override
        public void setTapActionListener(Class<? extends Activity> activity, Action action) {
            _mappedActions.put(TAP_ACTION_NAME, action);

            PendingIntent pendingIntent = createPendingIntent(activity, TAP_ACTION_NAME);

            _builder.setContentIntent(pendingIntent);
        }

        @Override
        public void addActionListener(Class<? extends Activity> activity, @DrawableRes int icon, String title, Action action) {
            _mappedActions.put(title, action);

            PendingIntent pendingIntent = createPendingIntent(activity, title);

            _builder.addAction(icon, title, pendingIntent);
        }

        @Override
        public void setOnDismissListener(Action dismissAction) {
            _dismissAction = dismissAction;
        }

        private PendingIntent createPendingIntent(Class<? extends Activity> activity, String actionName) {
            if (_notificationId == INVALID_NOTIFICATION_ID) {
                throw new IllegalStateException("Attempted to create pending intent before notification ID set");
            }

            return createBoundActionIntent(_notificationId, activity, actionName);
        }

        @Override
        public <U> void bind(Flowable<Optional<U>> source, final U ifEmpty, final Consumer<U> binding) {
            bind(source.map(new Function<Optional<U>, U>() {
                @Override
                public U apply(Optional<U> optionalValue) throws Exception {
                    return optionalValue.orElse(ifEmpty);
                }
            }).startWith(ifEmpty), binding);
        }

        @Override
        public <U> void bind(Flowable<U> source, final Consumer<U> binding) {
            _localDisposable.add(source.subscribe(new Consumer<U>() {
                @Override
                public void accept(U value) throws Exception {
                    binding.accept(value);
                    invalidate();
                }
            }));
        }

        @Override
        public void dispose() {
            _localDisposable.dispose();
        }

        @Override
        public boolean isDisposed() {
            return _localDisposable.isDisposed();
        }

        private int getNotificationId() {
            return _notificationId;
        }

        @Override
        public void setNotificationId(int notificationId) {
            _notificationId = notificationId;

            _builder.setDeleteIntent(createDismissIntent(notificationId));
        }

        @Override
        public NotificationCompat.Builder getBuilder() {
            return _builder;
        }

        void setup(T model) {
            _creator.createNotification(model, this);
            _isReady = true;

            if (_notificationId == INVALID_NOTIFICATION_ID) {
                throw new IllegalStateException("Notification ID not set before returning from creation method");
            }

            invalidateBinding(this);
        }

        private void invalidate() {
            if (_notificationId != INVALID_NOTIFICATION_ID && _isReady) {
                invalidateBinding(this);
            }
        }

        private void invokeAction(String actionName) {
            Action action = _mappedActions.get(actionName);

            if (action != null) {
                try {
                    action.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void onNotificationDismissed() {
            Action dismissAction = _dismissAction;

            if (dismissAction != null) {
                try {
                    dismissAction.run();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void notified() {
            _builder.setOnlyAlertOnce(true);
        }
    }

    private PendingIntent createDismissIntent(int notificationId) {
        Intent intent = new Intent()
                .addFlags(Intent.FLAG_FROM_BACKGROUND)
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(getActionDismissId());

        intent.putExtra(KEY_NOTIFICATION_ID, notificationId);

        return PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createBoundActionIntent(int notificationId, Class<? extends Activity> activity, String actionName) {
        Intent intent = new Intent(getContext(), activity)
                .addFlags(Intent.FLAG_FROM_BACKGROUND)
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(getActionModelEventId());

        intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
        intent.putExtra(KEY_MAPPED_ACTION, actionName);

        return PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public interface NotificationCreator<T> {
        void createNotification(T model, NotificationBinding binding);
    }

    private String _channelId;
    private final NotificationManagerCompat _notificationManager;

    private CompositeDisposable _listSubscription = new CompositeDisposable();
    private FlowableList<T> _boundList = null;
    private List<BaseNotificationBinding> _notificationBindings = new ArrayList<>();

    private NotificationCreator<T> _creator;

    private final Context _context;

    private String getActionDismissId() {
        return getChannelId() + ".ACTION_MESSAGE_DISMISS";
    }

    private String getActionModelEventId() {
        return getChannelId() + ".ACTION_MODEL_EVENT";
    }

    private Context getContext() {
        return _context;
    }

    private String getChannelId() {
        return _channelId;
    }

    public NotificationBindingHandler(String channelId, Context context) {
        _context = context;
        _channelId = channelId;
        _notificationManager = NotificationManagerCompat.from(context);

        setupNotificationChannel(context);

        context.registerReceiver(this, new IntentFilter(getActionDismissId()));
        context.registerReceiver(this, new IntentFilter(getActionModelEventId()));
    }

    public void setList(FlowableList<T> list) {
        FlowableList<T> previousList = _boundList;

        _listSubscription.dispose();
        _boundList = list;

        if (list != null) {
            Disposable subscription = list.updates().subscribe(new Consumer<Update<T>>() {
                @Override
                public void accept(Update<T> update) throws Exception {
                    for (Change change : update.changes) {
                        switch (change.type) {
                            case Inserted: {
                                BaseNotificationBinding binding = bindItem(update.list.get(change.to));
                                _notificationBindings.add(change.to, binding);
                                break;
                            }
                            case Moved: {
                                BaseNotificationBinding moved = _notificationBindings.remove(change.from);
                                _notificationBindings.add(change.to, moved);
                                break;
                            }
                            case Reloaded: {
                                _notificationManager.cancelAll();

                                List<BaseNotificationBinding> notificationBindings = _notificationBindings;

                                List<BaseNotificationBinding> reloadedNotificationBindings = new ArrayList<>();

                                for (BaseNotificationBinding binding : notificationBindings) {
                                    binding.dispose();
                                }

                                for (T model : update.list) {
                                    BaseNotificationBinding binding = bindItem(model);

                                    reloadedNotificationBindings.add(binding);
                                }

                                _notificationBindings = reloadedNotificationBindings;

                                break;
                            }
                            case Removed: {
                                BaseNotificationBinding removed = _notificationBindings.remove(change.from);

                                removed.dispose();
                                break;
                            }
                        }
                    }
                }
            });

            _listSubscription.add(subscription);
        }
    }

    public void setCreator(NotificationCreator<T> creator) {
        _creator = creator;
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
                NotificationChannel channel = createNotificationChannel(getChannelId(), "Test", NotificationManager.IMPORTANCE_DEFAULT, "Something");
                notificationService.createNotificationChannel(channel);
            }
        }
    }

    private void notifyBinding(BaseNotificationBinding binding) {
        NotificationCompat.Builder builder = binding.getBuilder();

        _notificationManager.notify(binding.getNotificationId(), builder.build());

        binding.notified();
    }

    private void invalidateBinding(BaseNotificationBinding binding) {
        notifyBinding(binding);
    }

    private BaseNotificationBinding bindItem(T model) {
        BaseNotificationBinding binding = new BaseNotificationBinding();

        binding.setup(model);

        return binding;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        if (getActionDismissId().equals(action)) {
            int notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, INVALID_NOTIFICATION_ID);

            if (notificationId == INVALID_NOTIFICATION_ID) {
                throw new IllegalStateException("Invalid notification ID provided");
            }

            for (BaseNotificationBinding binding : _notificationBindings) {
                if (binding.getNotificationId() == notificationId) {
                    binding.onNotificationDismissed();
                    break;
                }
            }
        }
        else if (getActionModelEventId().equals(action)) {
            int notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, INVALID_NOTIFICATION_ID);
            String actionName = intent.getStringExtra(KEY_MAPPED_ACTION);

            if (notificationId == INVALID_NOTIFICATION_ID) {
                throw new IllegalStateException("Invalid notification ID provided");
            }

            for (BaseNotificationBinding binding : _notificationBindings) {
                if (binding.getNotificationId() == notificationId) {
                    binding.invokeAction(actionName);
                    break;
                }
            }
        }
    }
}
