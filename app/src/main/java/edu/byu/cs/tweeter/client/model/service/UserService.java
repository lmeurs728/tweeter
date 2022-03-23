package edu.byu.cs.tweeter.client.model.service;


import android.os.Bundle;
import android.os.Handler;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.*;
import edu.byu.cs.tweeter.model.net.response.*;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

import java.io.IOException;

public class UserService extends BaseService {
    private static final String URL_PATH_LOGIN = "/login";
    private static final String URL_PATH_LOGOUT = "/logout";
    private static final String URL_PATH_GET_USER = "/get-user";
    private static final String URL_PATH_REGISTER = "/register";
    static final String URL_PATH_UNFOLLOW = "/unfollow";

    private final Observer observer;

    public UserService(Observer observer) {
        this.observer = observer;
    }


    public interface Observer extends BaseObserver {
        default void handleUserSuccess(User user) {}
        default void handleLoginSuccess(User loggedInUser) {}
        default void handleRegisterSuccess(User registeredUser) {}
        default void handleLogoutSuccess() {}
    }

    public void getUser(String clickable) {
        GetUserTask getUserTask = new GetUserTask(
                clickable, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
        observer.sendMessage("Getting user's profile...");
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private static class GetUserHandler extends BaseHandler {
        private final Observer observer;

        public GetUserHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "get user's profile";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            User user = (User) bundle.getSerializable(GetUserTask.USER_KEY);
            observer.handleUserSuccess(user);
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }

    /**
     * Background task that returns the profile for a specified user.
     */
    public static class GetUserTask extends BackgroundTask {
        protected static final String LOG_TAG = "GetUserTask";
        public static final String USER_KEY = "user";

        /**
         * Alias (or handle) for user whose profile is being retrieved.
         */
        private final String alias;

        public GetUserTask(String alias, Handler messageHandler) {
            super(messageHandler);
            this.alias = alias;
        }

//        private FakeData getFakeData() {
//            return new FakeData();
//        }
//
//        private User getUser() {
//            User user = getFakeData().findUserByAlias(alias);
//            return user;
//        }

        @Override
        protected void runTask() throws IOException, TweeterRemoteException {
//            User user = getUser();
            GetUserRequest request = new GetUserRequest(alias);
            UserResponse response = getServerFacade().getUser(request, URL_PATH_GET_USER);
            if (response.isSuccess()) {
                sendSuccessMessage(msgBundle -> msgBundle.putSerializable(USER_KEY, response.getUser()));
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
    }

    /**
     * Background task that logs in a user (i.e., starts a session).
     */
    public static class LoginTask extends BackgroundTask {

        protected static final String LOG_TAG = "LoginTask";
        public static final String USER_KEY = "user";
        public static final String AUTH_TOKEN_KEY = "auth-token";

        /**
         * The user's username (or "alias" or "handle"). E.g., "@susan".
         */
        private final String username;
        /**
         * The user's password.
         */
        private final String password;

        public LoginTask(Handler messageHandler, String username, String password) {
            super(messageHandler);
            this.username = username;
            this.password = password;
        }
//            private Pair<User, AuthToken> doLogin() {
//            User loggedInUser = getFakeData().getFirstUser();
//            AuthToken authToken = getFakeData().getAuthToken();
//            return new Pair<>(loggedInUser, authToken);
//        }

        @Override
        protected void runTask() throws IOException, TweeterRemoteException {
            LoginRequest request = new LoginRequest(username, password);
            LoginResponse response = getServerFacade().login(request, URL_PATH_LOGIN);

            if(response.isSuccess()) {
                User loggedInUser = response.getUser();
                AuthToken authToken = response.getAuthToken();

                sendSuccessMessage(msgBundle -> {
                    msgBundle.putSerializable(USER_KEY, loggedInUser);
                    msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
                });
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
    }

    /**
     * Background task that logs out a user (i.e., ends a session).
     */
    public static class LogoutTask extends BackgroundTask {
        protected static final String LOG_TAG = "LogoutTask";

        public LogoutTask(Handler messageHandler) {
            super(messageHandler);
        }

        @Override
        protected void runTask() throws IOException, TweeterRemoteException {
            LogoutRequest logoutRequest = new LogoutRequest();
            LogoutResponse response = getServerFacade().logout(logoutRequest, URL_PATH_LOGOUT);
            if (response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
    }

    /**
     * Background task that creates a new user account and logs in the new user (i.e., starts a session).
     */
    public static class RegisterTask extends BackgroundTask {
        protected static final String LOG_TAG = "RegisterTask";

        public static final String USER_KEY = "user";
        public static final String AUTH_TOKEN_KEY = "auth-token";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * The user's first name.
         */
        private String firstName;
        /**
         * The user's last name.
         */
        private String lastName;
        /**
         * The user's username (or "alias" or "handle"). E.g., "@susan".
         */
        private String username;
        /**
         * The user's password.
         */
        private String password;
        /**
         * The base-64 encoded bytes of the user's profile image.
         */
        private String image;


        public RegisterTask(Handler messageHandler, String firstName, String lastName, String username, String password, String image) {
            super(messageHandler);
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.image = image;
        }

//        private FakeData getFakeData() {
//            return new FakeData();
//        }
//
//        private Pair<User, AuthToken> doRegister() {
//            User registeredUser = getFakeData().getFirstUser();
//            AuthToken authToken = getFakeData().getAuthToken();
//            return new Pair<>(registeredUser, authToken);
//        }

        @Override
        protected void runTask() throws IOException, TweeterRemoteException {
            RegisterRequest request = new RegisterRequest(firstName, lastName, username, password, image);
            RegisterResponse response = getServerFacade().register(request, URL_PATH_REGISTER);

            if (response.isSuccess()) {
                User registeredUser = response.getUser();
                AuthToken authToken = response.getAuthToken();

                sendSuccessMessage(msgBundle -> {
                    msgBundle.putSerializable(USER_KEY, registeredUser);
                    msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
                });
            }
            else {
                sendFailedMessage(response.getMessage());
            }

        }
    }

    /**
     * Background task that removes a following relationship between two users.
     */
    public static class UnfollowTask extends BackgroundTask {
        protected static final String LOG_TAG = "UnfollowTask";

        /**
         * Auth token for logged-in user.
         * This user is the "follower" in the relationship.
         */
        private AuthToken authToken;
        /**
         * The user that is being followed.
         */
        private User followee;

        public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
            super(messageHandler);
            this.authToken = authToken;
            this.followee = followee;
        }

        @Override
        protected void runTask() throws IOException, TweeterRemoteException {
            String followerAlias = Cache.getInstance().getCurrUser().getAlias();
            UnfollowRequest request = new UnfollowRequest(authToken, followerAlias, followee.getAlias());
            UnfollowResponse response = getServerFacade().unfollow(request, URL_PATH_UNFOLLOW);

            if (response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        }
    }

    public LoginTask getLoginTask(String username, String password) {
        return new LoginTask(new LoginHandler(observer), username, password);
    }

    public void login(String alias, String password) {
        // Send the login request.
        LoginTask loginTask = getLoginTask(alias, password);
        BackgroundTaskUtils.runTask(loginTask);
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private static class LoginHandler extends BaseHandler {
        Observer observer;
        
        public LoginHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "login";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            User loggedInUser = (User) bundle.getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(LoginTask.AUTH_TOKEN_KEY);

            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.handleLoginSuccess(loggedInUser);
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }

    public void register(String firstName, String lastName, String username, String password, String image) {
        // Send register request.
        RegisterTask registerTask = getRegisterTask(firstName, lastName, username, password, image);
        BackgroundTaskUtils.runTask(registerTask);
    }

    private RegisterTask getRegisterTask(String firstName, String lastName, String username, String password, String image) {
        return new RegisterTask(new RegisterHandler(observer), firstName, lastName, username, password, image);
    }

    private static class RegisterHandler extends BaseHandler {
        private final Observer observer;

        public RegisterHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "register";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            User registeredUser = (User) bundle.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.handleRegisterSuccess(registeredUser);
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }

    public void doLogoutTask() {
        LogoutTask logoutTask = new LogoutTask(new LogoutHandler(observer));
        BackgroundTaskUtils.runTask(logoutTask);
    }

    // LogoutHandler
    private static class LogoutHandler extends BaseHandler {
        Observer observer;

        LogoutHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "logout";
        }

        @Override
        protected void onSuccess(Bundle msg) {
            observer.handleLogoutSuccess();
        }

        @Override
        protected void onFailure(String message) {
            observer.sendMessage(message);
        }
    }
}
