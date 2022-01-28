package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private final Observer observer;

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface Observer {
        default void handleSuccess(List<User> followees, boolean hasMorePages){}
        void handleFailure(String message);
        default void handleException(Exception exception){}
        default void handleFollowersCountSuccess(String s){}
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

        this.observer = observer;
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
        GetFollowingTask followingTask = getGetFollowingTask(authToken, targetUser, limit, lastFollowee);
        BackgroundTaskUtils.runTask(followingTask);
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower) {
        GetFollowersTask getFollowersTask = getGetFollowersTask(authToken, targetUser, limit, lastFollower);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    /**
     * Returns an instance of {@link GetFollowingTask}. Allows mocking of the
     * GetFollowingTask class for testing purposes. All usages of GetFollowingTask
     * should get their instance from this method to allow for proper mocking.
     *
     * @return the instance.
     */
    // This method is public so it can be accessed by test cases
    public GetFollowingTask getGetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        return new GetFollowingTask(authToken, targetUser, limit, lastFollowee,
                new MessageHandler(Looper.getMainLooper(), observer));
    }

    public GetFollowersTask getGetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower) {
        return new GetFollowersTask(authToken, targetUser, limit, lastFollower,
                new GetFollowersHandler(Looper.getMainLooper(), observer));
    }

    public static class MessageHandler extends Handler {
        private final Observer observer;

        public MessageHandler(Looper looper, Observer observer) {
            super(looper);
            this.observer = observer;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) bundle.getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = bundle.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                observer.handleSuccess(followees, hasMorePages);
            } else if (bundle.containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String errorMessage = bundle.getString(GetFollowingTask.MESSAGE_KEY);
                observer.handleFailure(errorMessage);
            } else if (bundle.containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) bundle.getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }
    }

    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private static class GetFollowersHandler extends Handler {
        private final Observer observer;

        public GetFollowersHandler(Looper looper, Observer observer) {
            super(looper);
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                observer.handleSuccess(followers, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.handleFailure("Failed to get followers: " + message);
//                Toast.makeText(getContext(), "Failed to get followers: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.handleFailure("Failed to get followers because of exception: " + ex.getMessage());
//                Toast.makeText(getContext(), "Failed to get followers because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Background task that retrieves a page of other users being followed by a specified user.
     */
    public static class GetFollowingTask extends BackgroundTask {
        private static final String LOG_TAG = "GetFollowingTask";

//        public static final String SUCCESS_KEY = "success";
        public static final String FOLLOWEES_KEY = "followees";
        public static final String MORE_PAGES_KEY = "more-pages";
//        public static final String MESSAGE_KEY = "message";
//        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * The user whose following is being retrieved.
         * (This can be any user, not just the currently logged-in user.)
         */
        private User targetUser;
        /**
         * Maximum number of followed users to return (i.e., page size).
         */
        private int limit;
        /**
         * The last person being followed returned in the previous page of results (can be null).
         * This allows the new page to begin where the previous page ended.
         */
        private User lastFollowee;
//        /**
//         * Message handler that will receive task results.
//         */
//        private Handler messageHandler;

        public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                                Handler messageHandler) {
            super(messageHandler);

            this.authToken = authToken;
            this.targetUser = targetUser;
            this.limit = limit;
            this.lastFollowee = lastFollowee;
//            this.messageHandler = messageHandler;
        }

        @Override
        protected void runTask() {
            try {
                Pair<List<User>, Boolean> pageOfUsers = getFollowees();

                List<User> followees = pageOfUsers.getFirst();
                boolean hasMorePages = pageOfUsers.getSecond();

                sendSuccessMessage(followees, hasMorePages);

            } catch (Exception ex) {
                Log.e(LOG_TAG, "Failed to get followees", ex);
                sendExceptionMessage(ex);
            }
        }

        private void sendSuccessMessage(List<User> followees, boolean hasMorePages) {
            sendSuccessMessage(msgBundle -> {
                msgBundle.putSerializable(FOLLOWEES_KEY, (Serializable) followees);
                msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
            });

        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private Pair<List<User>, Boolean> getFollowees() {
            return getFakeData().getPageOfUsers((User) lastFollowee, limit, targetUser);
        }

//        // This method is public so it can be accessed by test cases
//        public void loadImages(List<User> followees) throws IOException {
//            for (User u : followees) {
//                BackgroundTaskUtils.loadImage(u);
//            }
//        }

//            Bundle msgBundle = new Bundle();
//            msgBundle.putBoolean(SUCCESS_KEY, true);
//            msgBundle.putSerializable(FOLLOWEES_KEY, (Serializable) followees);
//            msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
//
//            Message msg = Message.obtain();
//            msg.setData(msgBundle);
//
//            messageHandler.sendMessage(msg);
//        }

//        private void sendFailedMessage(String message) {
//            Bundle msgBundle = new Bundle();
//            msgBundle.putBoolean(SUCCESS_KEY, false);
//            msgBundle.putString(MESSAGE_KEY, message);
//
//            Message msg = Message.obtain();
//            msg.setData(msgBundle);
//
//            messageHandler.sendMessage(msg);
//        }
//
//        private void sendExceptionMessage(Exception exception) {
//            Bundle msgBundle = new Bundle();
//            msgBundle.putBoolean(SUCCESS_KEY, false);
//            msgBundle.putSerializable(EXCEPTION_KEY, exception);
//
//            Message msg = Message.obtain();
//            msg.setData(msgBundle);
//
//            messageHandler.sendMessage(msg);
//        }

    }

    /**
     * Background task that queries how many followers a user has.
     */
    public static class GetFollowersCountTask implements Runnable {
        private static final String LOG_TAG = "GetFollowersCountTask";

        public static final String SUCCESS_KEY = "success";
        public static final String COUNT_KEY = "count";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * The user whose follower count is being retrieved.
         * (This can be any user, not just the currently logged-in user.)
         */
        private User targetUser;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
            this.authToken = authToken;
            this.targetUser = targetUser;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {

                sendSuccessMessage(20);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private void sendSuccessMessage(int count) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putInt(COUNT_KEY, count);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    /**
     * Background task that retrieves a page of followers.
     */
    public static class GetFollowersTask implements Runnable {
        private static final String LOG_TAG = "GetFollowersTask";

        public static final String SUCCESS_KEY = "success";
        public static final String FOLLOWERS_KEY = "followers";
        public static final String MORE_PAGES_KEY = "more-pages";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * The user whose followers are being retrieved.
         * (This can be any user, not just the currently logged-in user.)
         */
        private User targetUser;
        /**
         * Maximum number of followers to return (i.e., page size).
         */
        private int limit;
        /**
         * The last follower returned in the previous page of results (can be null).
         * This allows the new page to begin where the previous page ended.
         */
        private User lastFollower;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                                Handler messageHandler) {
            this.authToken = authToken;
            this.targetUser = targetUser;
            this.limit = limit;
            this.lastFollower = lastFollower;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {
                Pair<List<User>, Boolean> pageOfUsers = getFollowers();

                List<User> followers = pageOfUsers.getFirst();
                boolean hasMorePages = pageOfUsers.getSecond();

                sendSuccessMessage(followers, hasMorePages);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private Pair<List<User>, Boolean> getFollowers() {
            Pair<List<User>, Boolean> pageOfUsers = getFakeData().getPageOfUsers(lastFollower, limit, targetUser);
            return pageOfUsers;
        }

        private void sendSuccessMessage(List<User> followers, boolean hasMorePages) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putSerializable(FOLLOWERS_KEY, (Serializable) followers);
            msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

    }

    /**
     * Background task that queries how many other users a specified user is following.
     */
    public static class GetFollowingCountTask implements Runnable {
        private static final String LOG_TAG = "GetFollowingCountTask";

        public static final String SUCCESS_KEY = "success";
        public static final String COUNT_KEY = "count";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * The user whose following count is being retrieved.
         * (This can be any user, not just the currently logged-in user.)
         */
        private User targetUser;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
            this.authToken = authToken;
            this.targetUser = targetUser;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {

                sendSuccessMessage(20);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private void sendSuccessMessage(int count) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putInt(COUNT_KEY, count);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    /**
     * Background task that establishes a following relationship between two users.
     */
    public static class FollowTask implements Runnable {
        private static final String LOG_TAG = "FollowTask";

        public static final String SUCCESS_KEY = "success";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         * This user is the "follower" in the relationship.
         */
        private AuthToken authToken;
        /**
         * The user that is being followed.
         */
        private User followee;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
            this.authToken = authToken;
            this.followee = followee;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {

                sendSuccessMessage();

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private void sendSuccessMessage() {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    /**
     * Background task that determines if one user is following another.
     */
    public static class IsFollowerTask implements Runnable {
        private static final String LOG_TAG = "IsFollowerTask";

        public static final String SUCCESS_KEY = "success";
        public static final String IS_FOLLOWER_KEY = "is-follower";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * The alleged follower.
         */
        private User follower;
        /**
         * The alleged followee.
         */
        private User followee;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
            this.authToken = authToken;
            this.follower = follower;
            this.followee = followee;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {

                sendSuccessMessage(new Random().nextInt() > 0);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private void sendSuccessMessage(boolean isFollower) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    public void doGetFollowersCountTask(User selectedUser) {
        FollowService.GetFollowersCountTask followersCountTask = new FollowService.GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowersCountHandler());
        BackgroundTaskUtils.runTask(followersCountTask);
    }

    // GetFollowersCountHandler

    private class GetFollowersCountHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowService.GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(FollowService.GetFollowersCountTask.COUNT_KEY);
                observer.handleFollowersCountSuccess(String.valueOf(count));
//                followerCount.setText(getString(R.string.followerCount, String.valueOf(count)));
            } else if (msg.getData().containsKey(FollowService.GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowService.GetFollowersCountTask.MESSAGE_KEY);
                observer.handleFailure("Failed to get followers count: " + message);
//                Toast.makeText(MainActivity.this, "Failed to get followers count: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(FollowService.GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowService.GetFollowersCountTask.EXCEPTION_KEY);
                observer.handleFailure("Failed to get followers count because of exception: " + ex.getMessage());
//                Toast.makeText(MainActivity.this, "Failed to get followers count because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
