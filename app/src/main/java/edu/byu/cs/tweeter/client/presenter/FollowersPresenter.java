package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter implements UserService.Observer, FollowService.Observer {

    private static final String LOG_TAG = "FollowersPresenter";
    private static final int PAGE_SIZE = 10;

    private final User user;

    UserService userService;
    private final View view;

    public void setLastFollower(User lastFollower) {
        this.lastFollower = lastFollower;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    private User lastFollower;

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    private boolean isLoading = false;
    private boolean hasMorePages = true;

    @Override
    public void handleSuccess(List<User> followers, boolean hasMorePages) {
        setLastFollower((followers.size() > 0) ? followers.get(followers.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(followers);
        setLoading(false);
    }

    @Override
    public void handleFailure(String message) {
        String errorMessage = "Failed to retrieve followers: " + message;
        Log.e(LOG_TAG, errorMessage);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void handleException(Exception exception) {
        String errorMessage = "Failed to retrieve followers because of exception: " + exception.getMessage();
        Log.e(LOG_TAG, errorMessage, exception);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    public void getUsersProfile(String handle) {
        userService.getUser(handle);
    }

    @Override
    public void handleUserSuccess(User user) {
        view.startActivity(user);
    }

    @Override
    public void sendMessage(String message) {
        view.displayMessage(message);
    }

    @Override
    public void handleLoginSuccess(User loggedInUser) {
        // Do nothing
    }

    public interface View {
        void setLoading(boolean value);
        void addItems(List<User> newUsers);
        void displayErrorMessage(String message);

        void startActivity(User user);
        void displayMessage(String message);
    }



    public FollowersPresenter(View view, User user) {
        this.view = view;
        this.user = user;
        userService = new UserService(this);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower) {
        getFollowersService(this).getFollowers(authToken, targetUser, limit, lastFollower);
    }

    public FollowService getFollowersService(FollowService.Observer observer) {
        return new FollowService(observer);
    }

    /**
     * Called by the view to request that another page of "following" users be loaded.
     */
    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);

            getFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollower);
        }
    }
}
