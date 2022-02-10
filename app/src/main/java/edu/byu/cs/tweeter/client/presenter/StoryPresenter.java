package edu.byu.cs.tweeter.client.presenter;

<<<<<<< Updated upstream
public class StoryPresenter {
    public interface View {

    }

    private View view;

    public StoryPresenter(View view) {
        this.view = view;
=======
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

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
>>>>>>> Stashed changes
    }
}
