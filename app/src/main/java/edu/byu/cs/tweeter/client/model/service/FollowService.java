package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.Random;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private final Observer followObserver;

    public void doFollowTask() {
        FollowService.FollowTask followTask = new FollowService.FollowTask(
                new FollowHandler());
        BackgroundTaskUtils.runTask(followTask);
    }

    public void doUnfollowTask(User selectedUser) {
        UserService.UnfollowTask unfollowTask = new UserService.UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new UnfollowHandler());
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    public void doPostStatusTask() {
        StatusService.PostStatusTask statusTask = new StatusService.PostStatusTask(
                new PostStatusHandler());
        BackgroundTaskUtils.runTask(statusTask);
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
        default void handlePostStatusSuccess() {}
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
                new GetFollowingHandler(Looper.getMainLooper(), followObserver));
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
        protected Pair<List<User>, Boolean> getItems(int limit, User lastItem, User targetUser) {
            return getFakeData().getPageOfUsers(lastItem, limit, targetUser);
        }
    }

    /**
     * Background task that queries how many followers a user has.
     */
    public static class GetFollowersCountTask extends GetFollowCountTask {
        protected static final String LOG_TAG = "GetFollowersCountTask";
        public GetFollowersCountTask(Handler messageHandler) {
            super(messageHandler);
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
        protected Pair<List<User>, Boolean> getItems(int limit, User lastItem, User targetUser) {
            return getFakeData().getPageOfUsers(lastItem, limit, targetUser);
        }
    }

    /**
     * Background task that queries how many other users a specified user is following.
     */
    public static class GetFollowingCountTask extends GetFollowCountTask {
        protected static final String LOG_TAG = "GetFollowingCountTask";
        public GetFollowingCountTask(Handler messageHandler) {
            super(messageHandler);
        }
    }

    /**
     * Background task that establishes a following relationship between two users.
     */
    public static class FollowTask extends BackgroundTask {
        protected static final String LOG_TAG = "FollowTask";

        public FollowTask(Handler messageHandler) {
            super(messageHandler);
        }

        @Override
        protected void runTask() {
            sendSuccessMessage();
        }
    }

    /**
     * Background task that determines if one user is following another.
     */
    public static class IsFollowerTask extends BackgroundTask {
        protected static final String LOG_TAG = "IsFollowerTask";
        public static final String IS_FOLLOWER_KEY = "is-follower";

        public IsFollowerTask(Handler messageHandler) {
            super(messageHandler);
        }

        @Override
        protected void runTask() {
            BundleLoader bundleLoader = msgBundle -> msgBundle.putBoolean(IS_FOLLOWER_KEY, new Random().nextInt() > 0);
            sendSuccessMessage(bundleLoader);
        }
    }

    public void doGetFollowersCountTask() {
        FollowService.GetFollowersCountTask followersCountTask = new FollowService.GetFollowersCountTask(
                new GetFollowersCountHandler(followObserver));
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
            int count = bundle.getInt(FollowService.GetFollowersCountTask.COUNT_KEY);
            observer.handleFollowersCountSuccess(String.valueOf(count));
        }
    }

    public void doGetFollowingCountTask() {
        FollowService.GetFollowingCountTask followingCountTask = new FollowService.GetFollowingCountTask(
                new GetFollowingCountHandler());
        BackgroundTaskUtils.runTask(followingCountTask);
    }

    // GetFollowingCountHandler
    private class GetFollowingCountHandler extends BaseHandler {
        public GetFollowingCountHandler() {
            this.thingtoAccomplish = "get following count";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            int count = bundle.getInt(FollowService.GetFollowingCountTask.COUNT_KEY);
            followObserver.handleFollowingCountSuccess(String.valueOf(count));
        }
    }

    public void doIsFollowerTask(User selectedUser) {
        FollowService.IsFollowerTask isFollowerTask = new FollowService.IsFollowerTask(
                new IsFollowerHandler());
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    // IsFollowerHandler
    private class IsFollowerHandler extends BaseHandler {
        public IsFollowerHandler() {
            this.thingtoAccomplish = "determine following relationship";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            boolean isFollower = bundle.getBoolean(FollowService.IsFollowerTask.IS_FOLLOWER_KEY);
            followObserver.handleIsFollowerSuccess(isFollower);
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
    }

    // PostStatusHandler
    private class PostStatusHandler extends BaseHandler {
        PostStatusHandler() {
            this.thingtoAccomplish = "post status";
        }

        @Override
        protected void onSuccess(Bundle msg) {
            followObserver.handlePostStatusSuccess();
        }
    }

}
