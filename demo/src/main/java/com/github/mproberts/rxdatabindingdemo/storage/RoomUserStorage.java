package com.github.mproberts.rxdatabindingdemo.storage;

import com.github.mproberts.rxdatabindingdemo.data.User;
import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxdatabindingdemo.data.UserStorage;
import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.map.SubjectMap;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;

class RoomUserStorage implements UserStorage {
    private final SubjectMap<UserId, User> _userLookup = new SubjectMap<>();
    private final UserDao _userDao;
    private final Scheduler _storageScheduler;
    private final Map<String, User> _inMemoryUserCache = new LinkedHashMap<String, User>() {
        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > 50;
        }
    };

    public RoomUserStorage(UserDao userDao, Scheduler storageScheduler) {
        _userDao = userDao;
        _storageScheduler = storageScheduler;
        _userLookup.faults()
                .flatMap((userId) -> {
                    User userLookup;

                    synchronized (_inMemoryUserCache) {
                        if (!_inMemoryUserCache.containsKey(userId.value)) {
                            return Flowable.just(userId);
                        }

                        userLookup = _inMemoryUserCache.get(userId.value);
                    }

                    _userLookup.onNext(userId, userLookup);

                    return Flowable.empty();

                })
                .observeOn(storageScheduler)
                .subscribe((userId) -> {
                    RoomUser roomUser = _userDao.userById(userId);

                    if (roomUser != null) {
                        synchronized (_inMemoryUserCache) {
                            _inMemoryUserCache.put(userId.value, roomUser);
                        }

                        _userLookup.onNext(userId, roomUser);
                    }
                });
    }

    @Override
    public FlowableList<Flowable<User>> allContacts() {
        return FlowableList.diff(_userDao.allUsers()).map(_userLookup).cache(10, 10);
    }

    @Override
    public FlowableList<Flowable<User>> filteredContacts(String query) {
        return FlowableList.diff(_userDao.filteredUsers(query + "%")).map(_userLookup).cache(10, 10);
    }

    @Override
    public Flowable<User> userById(UserId userId) {
        return _userLookup.get(userId);
    }
}
