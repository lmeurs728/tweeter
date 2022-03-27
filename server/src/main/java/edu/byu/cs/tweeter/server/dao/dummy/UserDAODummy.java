//package edu.byu.cs.tweeter.server.dao.dummy;
//
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
//import edu.byu.cs.tweeter.model.net.request.LoginRequest;
//import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
//import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
//import edu.byu.cs.tweeter.model.net.response.LoginResponse;
//import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
//import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
//import edu.byu.cs.tweeter.model.net.response.UserResponse;
//import edu.byu.cs.tweeter.server.dao.UserDAO;
//import edu.byu.cs.tweeter.util.FakeData;
//
//public class UserDAODummy implements UserDAO {
//
//    public LoginResponse login(LoginRequest request) {
//        User user = getDummyUser();
//        AuthToken authToken = getDummyAuthToken();
//        return new LoginResponse(user, authToken);
//    }
//
//    public User register(RegisterRequest request) {
//        // TODO: Generates dummy data. Replace with a real implementation.
//        User user = getDummyUser();
//        AuthToken authToken = getDummyAuthToken();
//        return new RegisterResponse(user, authToken);
//    }
//
//    public UserResponse getUser(GetUserRequest request) {
//        return new UserResponse(getDummyUser());
//    }
//
//    public LogoutResponse logout(LogoutRequest request) {
//        return new LogoutResponse();
//    }
//
//    /**
//     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
//     * This is written as a separate method to allow mocking of the {@link FakeData}.
//     *
//     * @return a {@link FakeData} instance.
//     */
//    FakeData getFakeData() {
//        return new FakeData();
//    }
//
//    /**
//     * Returns the dummy user to be returned by the login operation.
//     * This is written as a separate method to allow mocking of the dummy user.
//     *
//     * @return a dummy user.
//     */
//    User getDummyUser() {
//        return getFakeData().getFirstUser();
//    }
//
//    /**
//     * Returns the dummy auth token to be returned by the login operation.
//     * This is written as a separate method to allow mocking of the dummy auth token.
//     *
//     * @return a dummy auth token.
//     */
//    AuthToken getDummyAuthToken() {
//        return getFakeData().getAuthToken();
//    }
//}
