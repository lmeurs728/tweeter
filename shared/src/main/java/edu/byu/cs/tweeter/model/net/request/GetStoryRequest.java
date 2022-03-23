package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStoryRequest {
    private AuthToken authToken;
    private Status lastStatus;
    private int limit;
    private String targetUser;

    public GetStoryRequest() {
    }

    public GetStoryRequest(AuthToken authToken, Status lastStatus, int limit, String targetUser) {
        this.authToken = authToken;
        this.lastStatus = lastStatus;
        this.limit = limit;
        this.targetUser = targetUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public Status getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
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
}
