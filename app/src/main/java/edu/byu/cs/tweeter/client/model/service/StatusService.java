package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {
    Observer observer;

    public StatusService(Observer observer) {
        this.observer = observer;
    }

    public void getStory(int limit, Status lastStatus) {
        GetStoryTask getStoryTask = new GetStoryTask(limit, lastStatus,
                new GetStoryHandler(observer));
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    public void doGetFeedTask(int PAGE_SIZE, Status lastItem) {
        GetFeedTask getFeedTask = new GetFeedTask(PAGE_SIZE, lastItem, new StatusService.GetFeedHandler(observer));
        BackgroundTaskUtils.runTask(getFeedTask);
    }

    /**
     * Background task that retrieves a page of statuses from a user's feed.
     */
    public static class GetFeedTask extends GetItemsTask<Status> {
        protected static final String LOG_TAG = "GetFeedTask";
        public static final String ITEMS_KEY = "statuses";

        public GetFeedTask(int limit, Status lastStatus,
                           Handler messageHandler) {
            super(limit, lastStatus, messageHandler);
        }

        @Override
        protected Pair<List<Status>, Boolean> getItems(int limit, Status lastItem) {
            return getFakeData().getPageOfStatus(lastItem, limit);
        }
    }

    /**
     * Background task that retrieves a page of statuses from a user's story.
     */
    public static class GetStoryTask extends GetItemsTask<Status> {
        protected static final String LOG_TAG = "GetStoryTask";
        public static final String ITEMS_KEY = "statuses";

        public GetStoryTask(int limit, Status lastStatus,
                           Handler messageHandler) {
            super(limit, lastStatus, messageHandler);
        }

        @Override
        protected Pair<List<Status>, Boolean> getItems(int limit, Status lastItem) {
            return getFakeData().getPageOfStatus(lastItem, limit);
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

        public PostStatusTask(Handler messageHandler) {
            super(messageHandler);
        }

        @Override
        protected void runTask() {
            sendSuccessMessage();
        }
    }

    public void getStatuses(int limit, Status lastStatus) {
        GetStoryTask getStoryTask = new GetStoryTask(limit, lastStatus, new GetStoryHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getStoryTask);
    }

    /**
     * Message handler (i.e., observer) for GetStoryTask.
     */
    private static class GetStoryHandler extends BaseHandler {
        protected Observer observer;

        private GetStoryHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "get story";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            List<Status> statuses = (List<Status>) bundle.getSerializable(GetStoryTask.ITEMS_KEY);
            boolean hasMorePages = bundle.getBoolean(GetStoryTask.MORE_PAGES_KEY);

            observer.addStatuses(statuses, hasMorePages);
        }

        @Override
        protected void onResponse() {
            observer.setLoading(false);
        }
    }

    public interface Observer extends BaseObserver {
        void setLoading(boolean b);
        default void addStatuses(List<Status> items, boolean hasMorePages) {}
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */
    public static class GetFeedHandler extends BaseHandler {
        private final Observer observer;

        public GetFeedHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "get feed";
        }

        @Override
        protected void onResponse() {
            observer.setLoading(false);
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            List<Status> statuses = (List<Status>) bundle.getSerializable(GetFeedTask.ITEMS_KEY);
            boolean hasMorePages = bundle.getBoolean(GetFeedTask.MORE_PAGES_KEY);

            observer.addStatuses(statuses, hasMorePages);
        }
    }
}
