package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

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
        void handleSuccess(List<User> followees, boolean hasMorePages);
        void handleFailure(String message);
        void handleException(Exception exception);
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
}
