package edu.byu.cs.tweeter.client.presenter;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTasks.GetFeedTask;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements UserService.Observer {

    private Status lastStatus;

    public boolean getHasMorePages() {
        return hasMorePages;
    }

    private boolean hasMorePages = true;

    private User user;

    private final int PAGE_SIZE = 10;

    @Override
    public void handleUserSuccess(User user) {
        view.startActivity(user);
    }

    @Override
    public void sendMessage(String message) {
        view.displayMessage(message);
    }

    public void loadMoreItems() {
        view.setLoading(true);
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new GetFeedHandler());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFeedTask);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public interface View {
        void setLoading(boolean value);
        void displayMessage(String message);
        void startActivity(User user);

        void addStatuses(List<Status> statuses);
    }

    private final View view;
    private final UserService userService;

    public FeedPresenter(View view) {
        this.view = view;
        userService = new UserService(this);
    }

    public void getUsersProfile(String handle) {
        userService.getUser(handle);
    }

    public void handleFeedMessage(Message msg) {
        GetFeedHandler getFeedHandler = new GetFeedHandler();
        getFeedHandler.handleMessage(msg);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    private class GetFeedHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            view.setLoading(false);

            boolean success = msg.getData().getBoolean(GetFeedTask.SUCCESS_KEY);
            if (success) {
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetFeedTask.STATUSES_KEY);
                hasMorePages = msg.getData().getBoolean(GetFeedTask.MORE_PAGES_KEY);

                lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;

                view.addStatuses(statuses);
            } else if (msg.getData().containsKey(GetFeedTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFeedTask.MESSAGE_KEY);
                view.displayMessage("Failed to get feed: " + message);
            } else if (msg.getData().containsKey(GetFeedTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFeedTask.EXCEPTION_KEY);
                view.displayMessage("Failed to get feed because of exception: " + ex.getMessage());
            }
        }
    }

}
