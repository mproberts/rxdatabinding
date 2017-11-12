package com.github.mproberts.rxdatabindingdemo.search.vm;

import com.github.mproberts.rxdatabindingdemo.data.User;
import com.github.mproberts.rxdatabindingdemo.data.UserStorage;
import com.github.mproberts.rxdatabindingdemo.profile.vm.ProfileViewModel;
import com.github.mproberts.rxtools.list.FlowableList;
import com.github.mproberts.rxtools.types.Optional;

import io.reactivex.Flowable;
import io.reactivex.processors.BehaviorProcessor;

public class DefaultSearchViewModel implements SearchViewModel {
    private final ProfileViewModel.Navigator _profileNavigator;
    private final BehaviorProcessor<String> _queryBehaviour = BehaviorProcessor.createDefault("");
    private final UserStorage _users;

    public DefaultSearchViewModel(UserStorage users, ProfileViewModel.Navigator profileNavigator) {
        _users = users;
        _profileNavigator = profileNavigator;
    }

    @Override
    public FlowableList<SearchListItemViewModel> searchList() {

        return FlowableList
                .flatten(_queryBehaviour.map(_users::filteredContacts))
                .map(DefaultSearchListItemViewModel::new);
    }

    @Override
    public void onQueryChanged(String query) {
        _queryBehaviour.onNext(query);
    }

    private class DefaultSearchListItemViewModel implements SearchListItemViewModel {
        private final Flowable<User> _user;

        public DefaultSearchListItemViewModel(Flowable<User> user) {
            _user = user;
        }

        @Override
        public Flowable<Optional<String>> displayName() {
            return _user
                    .map((userUpdate) -> Optional.of
                            (userUpdate.displayName()));
        }

        @Override
        public Flowable<Optional<String>> username() {
            return _user
                    .map((userUpdate) -> Optional.of(userUpdate.username()))
                    .defaultIfEmpty(Optional.empty());
        }

        @Override
        public Flowable<Optional<String>> profilePhoto() {
            return _user
                    .map((userUpdate) -> Optional.ofNullable(userUpdate.photoUrl()))
                    .defaultIfEmpty(Optional.empty());
        }

        @Override
        public Flowable<Boolean> isPremium() {
            return _user
                    .map((userUpdate) -> userUpdate.isPremium())
                    .defaultIfEmpty(false);
        }

        @Override
        public void onItemTapped() {
            _profileNavigator.navigateToProfile(_user.blockingFirst().id());
        }
    }
}
