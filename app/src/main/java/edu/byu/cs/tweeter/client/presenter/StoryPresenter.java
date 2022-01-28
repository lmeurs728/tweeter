package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter implements StatusService.Observer, UserService.Observer {

    private static final int PAGE_SIZE = 10;

    private User user;

    private Status lastStatus;
    private boolean hasMorePages = true;

    UserService userService;
    private View view;
    private boolean isLoading = false;

    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public void getStory() {
        AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();
        new StatusService(this).getStory(authToken, user, PAGE_SIZE, lastStatus);
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            isLoading = true;
            setLoading(true);

            new StatusService(this).getStatuses(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastStatus);
        }
    }

    public boolean getHasMorePages() {
        return hasMorePages;
    }

    public void getUsersProfile(String alias) {
        userService.getUser(alias);
    }

    @Override
    public void handleUserSuccess(User user) {
        view.startActivity(user);
    }

    @Override
    public void sendMessage(String message) {
        view.displayMessage(message);
    }

    public interface View {
        void setLoading(boolean b);
        void addStatuses(List<Status> statuses);
        void displayMessage(String s);
        void startActivity(User user);
    }

    public StoryPresenter(View view) {
        this.view = view;
        userService = new UserService(this);
    }

    @Override
    public void addStatuses(List<Status> statuses, boolean hasMorePages) {
        setLastStatus((statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.addStatuses(statuses);
    }

    @Override
    public void displayMessage(String s) {
        view.displayMessage(s);
    }

    @Override
    public void setLoading(boolean b) {
        view.setLoading(b);
    }

    public void setUser(User user) {
        this.user = user;
    }
}
