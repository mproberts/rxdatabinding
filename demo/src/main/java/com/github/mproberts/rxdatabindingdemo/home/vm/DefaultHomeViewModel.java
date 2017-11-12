package com.github.mproberts.rxdatabindingdemo.home.vm;

import com.github.mproberts.rxdatabindingdemo.data.User;
import com.github.mproberts.rxdatabindingdemo.data.UserStorage;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;
import com.github.mproberts.rxdatabindingdemo.search.vm.SearchViewModel;
import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;

public class DefaultHomeViewModel implements HomeViewModel {
    private final UserStorage _userStorage;
    private final ProfileViewModel.Navigator _profileNavigator;
    private final SearchViewModel.Navigator _searchNavigator;

    public DefaultHomeViewModel(UserStorage userStorage, ProfileViewModel.Navigator profileNavigator, SearchViewModel.Navigator searchNavigator) {
        _userStorage = userStorage;
        _profileNavigator = profileNavigator;
        _searchNavigator = searchNavigator;
    }

    @Override
    public FlowableList<ContactListItemViewModel> contactList() {
        return _userStorage.allContacts()
                .map(DefaultContactListItemViewModel::new);
    }

    @Override
    public void onSearchTapped() {
        _searchNavigator.navigateToSearch();
    }

    private class DefaultContactListItemViewModel implements ContactListItemViewModel {
        private final Flowable<User> _user;

        public DefaultContactListItemViewModel(Flowable<User> user) {
            _user = user;
        }

        @Override
        public Flowable<Optional<String>> displayName() {
            return _user
                    .map((userUpdate) -> Optional.of(userUpdate.displayName()))
                    .startWith(Optional.empty());
        }

        @Override
        public Flowable<Optional<String>> username() {
            return _user
                    .map((userUpdate) -> Optional.of(userUpdate.username()))
                    .startWith(Optional.empty());
        }

        @Override
        public Flowable<Optional<String>> profilePhoto() {
            return _user
                    .map((userUpdate) -> Optional.ofNullable(userUpdate.photoUrl()))
                    .startWith(Optional.empty());
        }

        @Override
        public Flowable<Boolean> isPremium() {
            return _user
                    .map((userUpdate) -> userUpdate.isPremium())
                    .startWith(false);
        }

        @Override
        public void onItemTapped() {
            _profileNavigator.navigateToProfile(_user.blockingFirst().id());
        }
    }
}
