package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }

    public RegisterResponse register(RegisterRequest input) {
        if(input.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(input.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }
        else if(input.getFirstName() == null) {
            throw new RuntimeException("[BadRequest] Missing a first name");
        }
        else if(input.getLastName() == null) {
            throw new RuntimeException("[BadRequest] Missing a last name");
        }
        else if(input.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing an image");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new RegisterResponse(user, authToken);
    }

    public UserResponse getUser(GetUserRequest input) {
        return new UserResponse(getDummyUser());
    }
}
