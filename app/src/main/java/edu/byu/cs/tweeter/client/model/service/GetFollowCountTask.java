package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

import java.io.IOException;

import static edu.byu.cs.tweeter.client.model.service.BaseService.getServerFacade;

public class GetFollowCountTask extends BackgroundTask {
    public static final String COUNT_KEY = "count";

    static final String URL_PATH_FOLLOWING_COUNT = "/get-following-count";
    static final String URL_PATH_FOLLOWER_COUNT = "/get-follower-count";

    User targetAlias;
    boolean isFollowers;

    public GetFollowCountTask(Handler messageHandler, User targetAlias, boolean isFollowers) {
        super(messageHandler);
        this.targetAlias = targetAlias;
        this.isFollowers = isFollowers;
    }

    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
        AuthToken authToken = Cache.getInstance().getCurrUserAuthToken();
        if (!isFollowers) {
            GetFollowingCountRequest request = new GetFollowingCountRequest(authToken, targetAlias);
            FollowingCountResponse response = getServerFacade().getFollowingCount(request, URL_PATH_FOLLOWING_COUNT);
            if (response.isSuccess()) {
                sendSuccessMessage(msgBundle -> msgBundle.putInt(COUNT_KEY, response.getNumFollowees()));
            } else {
                sendFailedMessage(response.getMessage());
            }
        } else {
            GetFollowerCountRequest request = new GetFollowerCountRequest(authToken, targetAlias);
            FollowersCountResponse response = getServerFacade().getFollowersCount(request, URL_PATH_FOLLOWER_COUNT);
            if (response.isSuccess()) {
                sendSuccessMessage(msgBundle -> msgBundle.putInt(COUNT_KEY, response.getNumFollowers()));
            } else {
                sendFailedMessage(response.getMessage());
            }
        }
    }
}
