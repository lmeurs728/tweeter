package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

import java.util.List;

public class StoryPresenter extends PagedPresenter<Status> implements StatusService.Observer {
    public StoryPresenter(PagedView<Status> view) {
        super(view);
    }

    @Override
    public void doServiceMethod(boolean firstTime) {
        new StatusService(this).getStory(PAGE_SIZE, lastItem, user, firstTime);
    }

    @Override
    public void addStatuses(List<Status> items, boolean hasMorePages) {
        setLastItemAndHasMorePages(items, hasMorePages);
        view.addItems(items);
    }
}
