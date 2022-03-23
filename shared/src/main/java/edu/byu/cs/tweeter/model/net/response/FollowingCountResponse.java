package edu.byu.cs.tweeter.model.net.response;

public class FollowingCountResponse extends Response {
    private int numFollowees;

    public FollowingCountResponse(int numFollowees) {
        super(true);
        this.numFollowees = numFollowees;
    }

    public FollowingCountResponse(String message) {
        super(false, message);
    }

    public int getNumFollowees() {
        return numFollowees;
    }
}
