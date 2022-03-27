package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

import java.util.List;

public class FeedPresenter extends PagedPresenter<Status> implements StatusService.Observer {
    public FeedPresenter(PagedView<Status> view) {
        super(view);
    }

    @Override
    public void doServiceMethod(boolean firstTime) {
        new StatusService(this).doGetFeedTask(PAGE_SIZE, lastItem, firstTime);
    }

    @Override
    public void addStatuses(List<Status> items, boolean hasMorePages) {
        setLastItemAndHasMorePages(items, hasMorePages);
        view.addItems(items);
    }
}
