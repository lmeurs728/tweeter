package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;

import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public abstract class GetItemsTask<T> extends BackgroundTask {
    protected static final String LOG_TAG = "GetFollowersTask";

    public String ITEMS_KEY = "";
    public static final String MORE_PAGES_KEY = "more-pages";

    /**
     * The user whose followers are being retrieved.
     * (This can be any user, not just the currently logged-in user.)
     */
    private User targetUser = null;
    /**
     * Maximum number of followers to return (i.e., page size).
     */
    private final int limit;
    /**
     * The last follower returned in the previous page of results (can be null).
     * This allows the new page to begin where the previous page ended.
     */
    private final T lastItem;

    public GetItemsTask(int limit, T lastItem, String ITEMS_KEY,
                            Handler messageHandler, User targetUser) {
        super(messageHandler);
        this.targetUser = targetUser;
        this.limit = limit;
        this.lastItem = lastItem;
        this.ITEMS_KEY = ITEMS_KEY;
    }

    public GetItemsTask(int limit, T lastItem, String ITEMS_KEY,
                        Handler messageHandler) {
        super(messageHandler);
        this.limit = limit;
        this.lastItem = lastItem;
        this.ITEMS_KEY = ITEMS_KEY;
    }

    protected FakeData getFakeData() {
        return new FakeData();
    }

    abstract protected Pair<List<T>, Boolean> getItems(int limit, T lastItem, User targetUser);

    @Override
    protected void runTask() {
        Pair<List<T>, Boolean> pageOfItems = getItems(limit, lastItem, targetUser);

        List<T> items = pageOfItems.getFirst();
        boolean hasMorePages = pageOfItems.getSecond();

        sendSuccessMessage(msgBundle -> {
            msgBundle.putSerializable(ITEMS_KEY, (Serializable) items);
            msgBundle.putBoolean(MORE_PAGES_KEY, hasMorePages);
        });
    }

}
