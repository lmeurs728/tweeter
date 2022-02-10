package edu.byu.cs.tweeter.client.presenter;

<<<<<<< Updated upstream
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements UserService.Observer {

    @Override
    public void handleSuccess(User user) {
        view.startActivity(user);
    }

    @Override
    public void sendMessage(String message) {
        view.displayMessage(message);
    }

    public interface View {
        void displayMessage(String message);
        void startActivity(User user);
    }

    private View view;
    private UserService userService;

    public FeedPresenter(View view) {
        this.view = view;
        userService = new UserService(this);
    }

    public void getUsersProfile(String handle) {
        userService.getUser(handle);

    }



=======
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;

public class FeedPresenter extends PagedPresenter<Status> implements StatusService.Observer {
    public FeedPresenter(PagedView<Status> view) {
        super(view);
    }

    @Override
    public void doServiceMethod() {
        new StatusService(this).doGetFeedTask(PAGE_SIZE, lastItem);
    }

    @Override
    public void addStatuses(List<Status> items, boolean hasMorePages) {
        setLastItemAndHasMorePages(items, hasMorePages);
        view.addItems(items);
    }
>>>>>>> Stashed changes
}
