package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> implements UserService.Observer, FollowService.Observer {
    private static final String LOG_TAG = "FollowersPresenter";

    public FollowersPresenter(PagedView<User> view) {
        super(view);
    }

    @Override
    public void doServiceMethod() {
        new FollowService(this).getFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem);
    }

    @Override
    public void addFollows(List<User> items, boolean hasMorePages) {
        setLastItemAndHasMorePages(items, hasMorePages);
        view.addItems(items);

    }
}
