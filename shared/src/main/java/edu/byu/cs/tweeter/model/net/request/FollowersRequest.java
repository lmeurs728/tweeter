package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowersRequest {
    private AuthToken authToken;
    private String followeeAlias;
    private int limit;
    private String lastFollowerAlias;
    private boolean firstTime;

    public FollowersRequest() {
    }

    public FollowersRequest(AuthToken authToken, String followeeAlias, int limit, String lastFollowerAlias, boolean firstTime) {
        this.authToken = authToken;
        this.followeeAlias = followeeAlias;
        this.limit = limit;
        this.lastFollowerAlias = lastFollowerAlias;
        this.firstTime = firstTime;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLastFollowerAlias() {
        return lastFollowerAlias;
    }

    public void setLastFollowerAlias(String lastFollowerAlias) {
        this.lastFollowerAlias = lastFollowerAlias;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }
}
