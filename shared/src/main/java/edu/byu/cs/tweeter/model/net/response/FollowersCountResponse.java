package edu.byu.cs.tweeter.model.net.response;

public class FollowersCountResponse extends Response {
    private int numFollowers;

    public FollowersCountResponse(int numFollowers) {
        super(true);
        this.numFollowers = numFollowers;
    }

    public FollowersCountResponse(String message) {
        super(false, message);
    }

    public int getNumFollowers() {
        return numFollowers;
    }
}
