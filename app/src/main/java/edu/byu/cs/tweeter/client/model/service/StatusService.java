package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class StatusService extends BaseService {
    private static final String URL_PATH_GET_STORY = "/get-story";
    private static final String URL_PATH_GET_FEED = "/get-feed";
    private static final String URL_PATH_POST_STATUS = "/post-status";
    Observer observer;

    public StatusService(Observer observer) {
        this.observer = observer;
    }

    public void getStory(int limit, Status lastStatus, User targetUser, boolean firstTime) {
        GetStoryTask getStoryTask = new GetStoryTask(limit, lastStatus,
                new GetStoryHandler(observer), targetUser, firstTime);
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    public void doGetFeedTask(int PAGE_SIZE, Status lastItem, boolean firstTime) {
        GetFeedTask getFeedTask = new GetFeedTask(PAGE_SIZE, lastItem, new GetFeedHandler(observer), firstTime);
        BackgroundTaskUtils.runTask(getFeedTask);
    }

    /**
     * Background task that retrieves a page of statuses from a user's feed.
     */
    public static class GetFeedTask extends GetItemsTask<Status> {
        protected static final String LOG_TAG = "GetFeedTask";
        public static final String ITEMS_KEY = "statuses";

        public GetFeedTask(int limit, Status lastStatus,
                           Handler messageHandler, boolean firstTime) {
            super(limit, lastStatus, ITEMS_KEY, messageHandler, Cache.getInstance().getCurrUser(), firstTime);
        }

        @Override
        protected Pair<List<Status>, Boolean> getItems(int limit, Status lastStatus, User targetUser, AuthToken authToken) throws IOException, TweeterRemoteException {
            GetFeedRequest request = new GetFeedRequest(authToken, lastStatus == null ? 0 : new Date(lastStatus.getDate()).getTime(), limit, targetUser.getAlias(), firstTime);
            firstTime = false;
            FeedResponse response = getServerFacade().getFeed(request, URL_PATH_GET_FEED);

            if (response.isSuccess()) {
                List<Status> statuses = response.getStatuses();
                boolean hasMorePages = response.getHasMorePages();
                return new Pair<>(statuses, hasMorePages);
            }
            else {
                sendFailedMessage(response.getMessage());
            }

            return null;
        }
    }

    /**
     * Background task that retrieves a page of statuses from a user's story.
     */
    public static class GetStoryTask extends GetItemsTask<Status> {
        protected static final String LOG_TAG = "GetStoryTask";
        public static final String ITEMS_KEY = "statuses";

        public GetStoryTask(int limit, Status lastStatus,
                           Handler messageHandler, User targetUser, boolean firstTime) {
            super(limit, lastStatus, ITEMS_KEY, messageHandler, targetUser, firstTime);
        }

        @Override
        protected Pair<List<Status>, Boolean> getItems(int limit, Status lastItem, User targetUser, AuthToken authToken) throws IOException, TweeterRemoteException {
            GetStoryRequest request = new GetStoryRequest(authToken, lastItem == null ? 0 : new Date(lastItem.getDate()).getTime(), limit, targetUser.getAlias(), firstTime);
            firstTime = false;
            StoryResponse response = getServerFacade().getStory(request, URL_PATH_GET_STORY);

            if (response.isSuccess()) {
                List<Status> statuses = response.getStatuses();
                boolean hasMorePages = response.getHasMorePages();
                return new Pair<>(statuses, hasMorePages);
            }
            else {
                sendFailedMessage(response.getMessage());
            }

            return null;
        }
    }

    /**
     * Background task that posts a new status sent by a user.
     */
    public static class PostStatusTask extends BackgroundTask {
        protected static final String LOG_TAG = "PostStatusTask";

        public static final String SUCCESS_KEY = "success";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        private final Status status;

        public PostStatusTask(Handler messageHandler, Status status) {
            super(messageHandler);
            this.status = status;
        }

        @Override
        protected void runTask() throws IOException, TweeterRemoteException {
            AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();
            PostStatusRequest request = new PostStatusRequest(authToken, status);
            PostStatusResponse response = getServerFacade().postStatus(request, URL_PATH_POST_STATUS);

            if (response.isSuccess()){
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private static class GetStoryHandler extends GetItemsHandler<Status> {
        protected Observer observer;

        private GetStoryHandler(Observer observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
            this.thingtoAccomplish = "get story";
            this.key = GetStoryTask.ITEMS_KEY;
        }

        @Override
        public void addStuff(List<Status> items, boolean hasMorePages) {
            observer.addStatuses(items, hasMorePages);
        }

        @Override
        protected void onResponse() {
            observer.setLoading(false);
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }

    public interface Observer extends BaseObserver {
        void setLoading(boolean b);
        default void handlePostStatusSuccess() {}
        default void handlePostStatusFailure(String message) {}
        default void addStatuses(List<Status> items, boolean hasMorePages) {}
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    public static class GetFeedHandler extends GetItemsHandler<Status> {
        private final Observer observer;

        public GetFeedHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "get feed";
            this.key = GetFeedTask.ITEMS_KEY;
        }

        @Override
        protected void onResponse() {
            observer.setLoading(false);
        }

        @Override
        public void addStuff(List<Status> items, boolean hasMorePages) {
            observer.addStatuses(items, hasMorePages);
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }

    public void doPostStatusTask(Status status) {
        PostStatusTask statusTask = new PostStatusTask(
                new PostStatusHandler(observer), status);
        BackgroundTaskUtils.runTask(statusTask);
    }

    // PostStatusHandler
    private static class PostStatusHandler extends BaseHandler {
        public Observer observer;

        PostStatusHandler(Observer observer) {
            this.thingtoAccomplish = "post status";
            this.observer = observer;
        }

        @Override
        protected void onSuccess(Bundle msg) {
            observer.handlePostStatusSuccess();
        }

        @Override
        protected void onFailure(String msg) {
            observer.handlePostStatusFailure(msg);
        }
    }
}
