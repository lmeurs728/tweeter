package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class StoryPresenter extends PagedPresenter<Status> implements StatusService.Observer {
    public StoryPresenter(PagedView<Status> view) {
        super(view);
    }

    @Override
    public void doServiceMethod() {
        new StatusService(this).getStory(PAGE_SIZE, lastItem);
    }

    @Override
    public void addStatuses(List<Status> items, boolean hasMorePages) {
        setLastItemAndHasMorePages(items, hasMorePages);
        view.addItems(items);
    }
}
