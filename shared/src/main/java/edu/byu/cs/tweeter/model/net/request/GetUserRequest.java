package edu.byu.cs.tweeter.model.net.request;

public class GetUserRequest {
    private String alias;

    public GetUserRequest() {
    }

    public GetUserRequest(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
