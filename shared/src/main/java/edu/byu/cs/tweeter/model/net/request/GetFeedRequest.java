package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetFeedRequest {
    private AuthToken authToken;
    private long lastStatusTime;
    private int limit;
    private String targetUser;
    private boolean firstTime;

    public GetFeedRequest() {
    }

    public GetFeedRequest(AuthToken authToken, long lastStatusTime, int limit, String targetUser, boolean firstTime) {
        this.authToken = authToken;
        this.lastStatusTime = lastStatusTime;
        this.limit = limit;
        this.targetUser = targetUser;
        this.firstTime = firstTime;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public long getLastStatusTime() {
        return lastStatusTime;
    }

    public void setLastStatusTime(long lastStatusTime) {
        this.lastStatusTime = lastStatusTime;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }
}
