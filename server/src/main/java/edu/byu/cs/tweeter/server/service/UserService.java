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
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class UserService extends BaseService {
    public LogoutResponse logout(LogoutRequest request) {
        getAuthDAO().deleteAuthToken(request.getToken());
        return new LogoutResponse();
    }

    // TODO add error handling
    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null) {
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if (request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        User user;
        try {
            user = getUserDAO().login(request);
        } catch (Exception e) {
            return new LoginResponse(e.getMessage());
        }
        // get an auth token from the AuthDAO
        AuthToken auth = getAuthDAO().createAuthToken(request.getUsername());
        return new LoginResponse(user, auth);
    }

    // TODO: add error handling
    public RegisterResponse register(RegisterRequest input) {
        // validateRequest
        if(input.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        }
        else if(input.getPassword() == null) {
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
        // upload image to S3
        String imageURL = getImageDAO().uploadImage(input.getImage(), input.getUsername());
        // tell the UserDAO to register a new user
        User user = getUserDAO().register(input, imageURL);
        // get an auth token from the AuthDAO
        AuthToken auth = getAuthDAO().createAuthToken(input.getUsername());
        // return a RegisterResponse
        return new RegisterResponse(user, auth);
    }

    public UserResponse getUser(GetUserRequest input) {
        return getUserDAO().getUser(input);
    }

    public UserDAO getUserDAO() {
        return factory.getUserDAO();
    }

    public ImageDAO getImageDAO() {
        return factory.getImageDAO();
    }

    public AuthDAO getAuthDAO() {
        return factory.getAuthDAO();
    }
}
