package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends BaseService {

    static final String URL_PATH_FOLLOWING = "/get-followees";
    static final String URL_PATH_FOLLOWERS = "/get-followers";
    static final String URL_PATH_FOLLOW = "/follow";
    private static final String URL_PATH_IS_FOLLOWER = "/is-follower";

    private final Observer followObserver;

    public void doFollowTask(String followeeAlias) {
        FollowTask followTask = new FollowTask(
                new FollowHandler(), followeeAlias);
        BackgroundTaskUtils.runTask(followTask);
    }

    public void doUnfollowTask(User selectedUser) {
        UserService.UnfollowTask unfollowTask = new UserService.UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new UnfollowHandler());
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface Observer extends BaseObserver {
        default void handleFollowersCountSuccess(String s){}
        default void handleFollowingCountSuccess(String s){}
        default void handleIsFollowerSuccess(boolean isFollower){}
        default void handleFollowSuccess(){}
        default void enableFollowButton(){}
        default void handleUnfollowSuccess(){}
        default void addFollows(List<User> items, boolean hasMorePages) {}
    }

    /**
     * Creates an instance.
     *
     * @param observer the observer who wants to be notified when any asynchronous operations complete.
     */
    public FollowService(Observer observer) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(observer == null) {
            throw new NullPointerException();
        }

        this.followObserver = observer;
    }

    /**
     * Requests the users that the user specified in the request is following.
     * Limits the number of followees returned and returns the next set of
     * followees after any that were returned in a previous request.
     * This is an asynchronous operation.
     *
     * @param authToken the session auth token.
     * @param targetUser the user for whom followees are being retrieved.
     * @param limit the maximum number of followees to return.
     * @param lastFollowee the last followee returned in the previous request (can be null).
     */
    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        GetFollowingTask followingTask = getGetFollowingTask(targetUser, limit, lastFollowee);
        BackgroundTaskUtils.runTask(followingTask);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower) {
        GetFollowersTask getFollowersTask = getGetFollowersTask(targetUser, limit, lastFollower);
        BackgroundTaskUtils.runTask(getFollowersTask);
    }

    /**
     * Returns an instance of {@link GetFollowingTask}. Allows mocking of the
     * GetFollowingTask class for testing purposes. All usages of GetFollowingTask
     * should get their instance from this method to allow for proper mocking.
     *
     * @return the instance.
     */
    // This method is public so it can be accessed by test cases
    public GetFollowingTask getGetFollowingTask(User targetUser, int limit, User lastFollowee) {
        return new GetFollowingTask(targetUser, limit, lastFollowee,
                new GetFollowingHandler(Looper.getMainLooper(), followObserver)
        );
    }

    public GetFollowersTask getGetFollowersTask(User targetUser, int limit, User lastFollower) {
        return new GetFollowersTask(targetUser, limit, lastFollower,
                new GetFollowersHandler(Looper.getMainLooper(), followObserver));
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private static class GetFollowingHandler extends GetItemsHandler<User> {
        private final Observer observer;

        public GetFollowingHandler(Looper looper, Observer observer) {
            super(looper);
            this.observer = observer;
            this.thingtoAccomplish = "get followees";
            this.key = GetFollowingTask.ITEMS_KEY;
        }

        @Override
        public void addStuff(List<User> items, boolean hasMorePages) {
            observer.addFollows(items, hasMorePages);
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private static class GetFollowersHandler extends GetItemsHandler<User> {
        private final Observer observer;

        public GetFollowersHandler(Looper looper, Observer observer) {
            super(looper);
            this.observer = observer;
            this.thingtoAccomplish = "get followers";
            this.key = GetFollowersTask.ITEMS_KEY;
        }

        @Override
        public void addStuff(List<User> items, boolean hasMorePages) {
            observer.addFollows(items, hasMorePages);
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }

    /**
     * Background task that retrieves a page of followers.
     */
    public static class GetFollowingTask extends GetItemsTask<User> {
        protected static final String LOG_TAG = "GetFollowersTask";
        public static final String ITEMS_KEY = "following";

        public GetFollowingTask(User targetUser, int limit, User lastFollower,
                                Handler messageHandler) {
            super(limit, lastFollower, ITEMS_KEY, messageHandler, targetUser);
        }

        @Override
        protected Pair<List<User>, Boolean> getItems(int limit, User lastItem, User targetUser, AuthToken authToken) throws IOException, TweeterRemoteException {
            FollowingRequest request = new FollowingRequest(authToken, targetUser.getAlias(), limit, lastItem != null ? lastItem.getAlias() : null);
            FollowingResponse response = getServerFacade().getFollowees(request, URL_PATH_FOLLOWING);

            if (response.isSuccess()) {
                List<User> followees = response.getFollowees();
                boolean hasMorePages = response.getHasMorePages();
                return new Pair<>(followees, hasMorePages);
            }
            else {
                sendFailedMessage(response.getMessage());
            }

            return null;
        }
    }

    /**
     * Background task that queries how many followers a user has.
     */
    public static class GetFollowersCountTask extends GetFollowCountTask {
        protected static final String LOG_TAG = "GetFollowersCountTask";
        public GetFollowersCountTask(Handler messageHandler, User target) {
            super(messageHandler, target, true);
        }
    }

    /**
     * Background task that retrieves a page of followers.
     */
    public static class GetFollowersTask extends GetItemsTask<User> {
        protected static final String LOG_TAG = "GetFollowersTask";
        public static final String ITEMS_KEY = "followers";

        public GetFollowersTask(User targetUser, int limit, User lastFollower,
                                Handler messageHandler) {
            super(limit, lastFollower, ITEMS_KEY, messageHandler, targetUser);
        }

        @Override
        protected Pair<List<User>, Boolean> getItems(int limit, User lastItem, User targetUser, AuthToken authToken) throws IOException, TweeterRemoteException {
            FollowersRequest request = new FollowersRequest(authToken, targetUser.getAlias(), limit, lastItem != null ? lastItem.getAlias() : null);
            FollowersResponse response = getServerFacade().getFollowers(request, URL_PATH_FOLLOWERS);

            if (response.isSuccess()) {
                List<User> followers = response.getFollowers();
                boolean hasMorePages = response.getHasMorePages();
                return new Pair<>(followers, hasMorePages);
            }
            else {
                sendFailedMessage(response.getMessage());
            }

            return null;
        }
    }

    /**
     * Background task that queries how many other users a specified user is following.
     */
    public static class GetFollowingCountTask extends GetFollowCountTask {
        protected static final String LOG_TAG = "GetFollowingCountTask";
        public GetFollowingCountTask(Handler messageHandler, User target) {
            super(messageHandler, target, false);
        }
    }

    /**
     * Background task that establishes a following relationship between two users.
     */
    public static class FollowTask extends BackgroundTask {
        protected static final String LOG_TAG = "FollowTask";
        private String followeeAlias;

        public FollowTask(Handler messageHandler, String followeeAlias) {
            super(messageHandler);
            this.followeeAlias = followeeAlias;
        }

        @Override
        protected void runTask() throws IOException, TweeterRemoteException {
            AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();
            String followerAlias = Cache.getInstance().getCurrUser().getAlias();
            FollowRequest request = new FollowRequest(authToken, followerAlias, followeeAlias);
            FollowResponse response = getServerFacade().follow(request, URL_PATH_FOLLOW);

            if (response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
    }

    /**
     * Background task that determines if one user is following another.
     */
    public static class IsFollowerTask extends BackgroundTask {
        protected static final String LOG_TAG = "IsFollowerTask";
        public static final String IS_FOLLOWER_KEY = "is-follower";

        User selectedUser;

        public IsFollowerTask(Handler messageHandler, User selectedUser) {
            super(messageHandler);
            this.selectedUser = selectedUser;
        }

        @Override
        protected void runTask() throws IOException, TweeterRemoteException {
            AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();
            String followerAlias = Cache.getInstance().getCurrUser().getAlias();
            IsFollowerRequest request = new IsFollowerRequest(authToken, followerAlias, selectedUser.getAlias());
            IsFollowerResponse response = getServerFacade().isFollower(request, URL_PATH_IS_FOLLOWER);

            if (response.isSuccess()) {
                BundleLoader bundleLoader = msgBundle -> msgBundle.putBoolean(IS_FOLLOWER_KEY, response.getIsFollower());
                sendSuccessMessage(bundleLoader);
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
    }

    public void doGetFollowersCountTask(User target) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(
                new GetFollowersCountHandler(followObserver), target);
        BackgroundTaskUtils.runTask(followersCountTask);
    }

    // GetFollowersCountHandler
    private static class GetFollowersCountHandler extends BaseHandler {
        Observer observer;

        public GetFollowersCountHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "get followers count";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            int count = bundle.getInt(GetFollowersCountTask.COUNT_KEY);
            observer.handleFollowersCountSuccess(String.valueOf(count));
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }

    public void doGetFollowingCountTask(User target) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(
                new GetFollowingCountHandler(), target);
        BackgroundTaskUtils.runTask(followingCountTask);
    }

    // GetFollowingCountHandler
    private class GetFollowingCountHandler extends BaseHandler {
        public GetFollowingCountHandler() {
            this.thingtoAccomplish = "get following count";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            int count = bundle.getInt(GetFollowingCountTask.COUNT_KEY);
            followObserver.handleFollowingCountSuccess(String.valueOf(count));
        }

        @Override
        protected void onFailure(String message) {
            followObserver.sendMessage(message);
        }
    }

    public void doIsFollowerTask(User selectedUser) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(
                new IsFollowerHandler(), selectedUser);
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    // IsFollowerHandler
    private class IsFollowerHandler extends BaseHandler {
        public IsFollowerHandler() {
            this.thingtoAccomplish = "determine following relationship";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            boolean isFollower = bundle.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            followObserver.handleIsFollowerSuccess(isFollower);
        }

        @Override
        protected void onFailure(String message) {
            followObserver.sendMessage(message);
        }
    }

    // FollowHandler
    private class FollowHandler extends BaseHandler {
        FollowHandler() {
            this.thingtoAccomplish = "follow";
        }

        @Override
        protected void onSuccess(Bundle msg) {
            followObserver.handleFollowSuccess();
        }

        @Override
        protected void afterHandledResponse() {
            followObserver.enableFollowButton();
        }

        @Override
        protected void onFailure(String message) {
            followObserver.sendMessage(message);
        }
    }

    // UnfollowHandler
    private class UnfollowHandler extends BaseHandler {
        UnfollowHandler() {
            this.thingtoAccomplish = "unfollow";
        }

        @Override
        protected void onSuccess(Bundle msg) {
            followObserver.handleUnfollowSuccess();
        }

        @Override
        protected void afterHandledResponse() {
            followObserver.enableFollowButton();
        }

        @Override
        protected void onFailure(String message) {
            followObserver.sendMessage(message);
        }
    }

}
