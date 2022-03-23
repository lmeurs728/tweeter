package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class UserResponse extends Response {
    private User user;

    public UserResponse(User user) {
        super(true);
        this.user = user;
    }

    public UserResponse(String message) {
        super(false, message);
    }

    public User getUser() {
        return user;
    }
}
