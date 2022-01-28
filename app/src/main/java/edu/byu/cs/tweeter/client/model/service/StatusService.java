package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.presenter.FollowersPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {
    Observer observer;

    public StatusService(Observer observer) {
        this.observer = observer;
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        GetStoryTask getStoryTask = new GetStoryTask(authToken, targetUser, limit, lastStatus,
                new GetStoryHandler(observer));
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    /**
     * Background task that retrieves a page of statuses from a user's feed.
     */
    public static class GetFeedTask implements Runnable {
        private static final String LOG_TAG = "GetFeedTask";

        public static final String SUCCESS_KEY = "success";
        public static final String STATUSES_KEY = "statuses";
        public static final String MORE_PAGES_KEY = "more-pages";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * The user whose feed is being retrieved.
         * (This can be any user, not just the currently logged-in user.)
         */
        private User targetUser;
        /**
         * Maximum number of statuses to return (i.e., page size).
         */
        private int limit;
        /**
         * The last status returned in the previous page of results (can be null).
         * This allows the new page to begin where the previous page ended.
         */
        private Status lastStatus;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                           Handler messageHandler) {
            this.authToken = authToken;
            this.targetUser = targetUser;
            this.limit = limit;
            this.lastStatus = lastStatus;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {
                Pair<List<Status>, Boolean> pageOfStatus = getFeed();

                List<Status> statuses = pageOfStatus.getFirst();
                boolean hasMorePages = pageOfStatus.getSecond();

                sendSuccessMessage(statuses, hasMorePages);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private Pair<List<Status>, Boolean> getFeed() {
            Pair<List<Status>, Boolean> pageOfStatus = getFakeData().getPageOfStatus(lastStatus, limit);
            return pageOfStatus;
        }

        private void sendSuccessMessage(List<Status> statuses, boolean hasMorePages) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putSerializable(STATUSES_KEY, (Serializable) statuses);
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
     * Background task that retrieves a page of statuses from a user's story.
     */
    public static class GetStoryTask implements Runnable {
        private static final String LOG_TAG = "GetStoryTask";

        public static final String SUCCESS_KEY = "success";
        public static final String STATUSES_KEY = "statuses";
        public static final String MORE_PAGES_KEY = "more-pages";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * The user whose story is being retrieved.
         * (This can be any user, not just the currently logged-in user.)
         */
        private User targetUser;
        /**
         * Maximum number of statuses to return (i.e., page size).
         */
        private int limit;
        /**
         * The last status returned in the previous page of results (can be null).
         * This allows the new page to begin where the previous page ended.
         */
        private Status lastStatus;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                            Handler messageHandler) {
            this.authToken = authToken;
            this.targetUser = targetUser;
            this.limit = limit;
            this.lastStatus = lastStatus;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {
                Pair<List<Status>, Boolean> pageOfStatus = getStory();

                List<Status> statuses = pageOfStatus.getFirst();
                boolean hasMorePages = pageOfStatus.getSecond();

                sendSuccessMessage(statuses, hasMorePages);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private Pair<List<Status>, Boolean> getStory() {
            Pair<List<Status>, Boolean> pageOfStatus = getFakeData().getPageOfStatus(lastStatus, limit);
            return pageOfStatus;
        }

        private void sendSuccessMessage(List<Status> statuses, boolean hasMorePages) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putSerializable(STATUSES_KEY, (Serializable) statuses);
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
     * Background task that posts a new status sent by a user.
     */
    public static class PostStatusTask implements Runnable {
        private static final String LOG_TAG = "PostStatusTask";

        public static final String SUCCESS_KEY = "success";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * The new status being sent. Contains all properties of the status,
         * including the identity of the user sending the status.
         */
        private Status status;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
            this.authToken = authToken;
            this.status = status;
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

    public void getStatuses(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        GetStoryTask getStoryTask = new GetStoryTask(authToken, targetUser, limit, lastStatus, new GetStoryHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getStoryTask);
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private class GetStoryHandler extends Handler {
        private boolean isLoading = false;
        private Observer observer;

        private GetStoryHandler(Observer observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            isLoading = false;
            observer.setLoading(false);

            boolean success = msg.getData().getBoolean(GetStoryTask.SUCCESS_KEY);
            if (success) {
                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.STATUSES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);

                observer.addStatuses(statuses, hasMorePages);
            } else if (msg.getData().containsKey(GetStoryTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetStoryTask.MESSAGE_KEY);
                observer.displayMessage("Failed to get story: " + message);
            } else if (msg.getData().containsKey(GetStoryTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetStoryTask.EXCEPTION_KEY);
                observer.displayMessage("Failed to get story because of exception: " + ex.getMessage());
            }
        }
    }

    public interface Observer {
        void addStatuses(List<Status> statuses, boolean hasMorePages);
        void displayMessage(String s);
        void setLoading(boolean b);
    }
}
