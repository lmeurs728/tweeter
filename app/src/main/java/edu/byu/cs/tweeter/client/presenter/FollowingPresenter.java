package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> implements FollowService.Observer {
    protected static final String LOG_TAG = "FollowingPresenter";

    public FollowingPresenter(PagedView<User> view) {
        super(view);
    }

    @Override
    public void doServiceMethod() {
        new FollowService(this).getFollowees(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem);
    }

    @Override
    public void addFollows(List<User> items, boolean hasMorePages) {
        setLastItemAndHasMorePages(items, hasMorePages);
        view.addItems(items);
    }
}
