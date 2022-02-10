package edu.byu.cs.tweeter.client.model.service;


import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class UserService {

    private final UserService.Observer observer;

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

        private FakeData getFakeData() {
            return new FakeData();
        }

        private User getUser() {
            User user = getFakeData().findUserByAlias(alias);
            return user;
        }

        @Override
        protected void runTask() {
            User user = getUser();
            sendSuccessMessage(msgBundle -> msgBundle.putSerializable(USER_KEY, user));
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
        private String username;
        /**
         * The user's password.
         */
        private String password;

        public LoginTask(Handler messageHandler) {
            super(messageHandler);
        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private Pair<User, AuthToken> doLogin() {
            User loggedInUser = getFakeData().getFirstUser();
            AuthToken authToken = getFakeData().getAuthToken();
            return new Pair<>(loggedInUser, authToken);
        }

        @Override
        protected void runTask() {
            Pair<User, AuthToken> loginResult = doLogin();

            User loggedInUser = loginResult.getFirst();
            AuthToken authToken = loginResult.getSecond();

            sendSuccessMessage(msgBundle -> {
                msgBundle.putSerializable(USER_KEY, loggedInUser);
                msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
            });
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
        protected void runTask() {
            sendSuccessMessage();
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

        public RegisterTask(Handler messageHandler) {
            super(messageHandler);
        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private Pair<User, AuthToken> doRegister() {
            User registeredUser = getFakeData().getFirstUser();
            AuthToken authToken = getFakeData().getAuthToken();
            return new Pair<>(registeredUser, authToken);
        }

        @Override
        protected void runTask() {
            Pair<User, AuthToken> registerResult = doRegister();

            User registeredUser = registerResult.getFirst();
            AuthToken authToken = registerResult.getSecond();

            sendSuccessMessage(msgBundle -> {
                msgBundle.putSerializable(USER_KEY, registeredUser);
                msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
            });
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
        protected void runTask() {
            sendSuccessMessage();
        }
    }

    public LoginTask getLoginTask() {
        return new LoginTask(new LoginHandler(observer));
    }

    public void login(String alias, String password) {
        // Send the login request.
        LoginTask loginTask = getLoginTask();
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
            User loggedInUser = (User) bundle.getSerializable(UserService.LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(UserService.LoginTask.AUTH_TOKEN_KEY);

            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.handleLoginSuccess(loggedInUser);
        }
    }

    public RegisterTask getRegisterTask() {
        return new RegisterTask(new RegisterHandler(observer));
    }

    public void register(String firstName, String lastName, String username, String password, String image) {
        // Send register request.
        RegisterTask registerTask = getRegisterTask();
        BackgroundTaskUtils.runTask(registerTask);
    }

    private static class RegisterHandler extends BaseHandler {
        private final Observer observer;

        public RegisterHandler(Observer observer) {
            this.observer = observer;
            this.thingtoAccomplish = "register";
        }

        @Override
        protected void onSuccess(Bundle bundle) {
            User registeredUser = (User) bundle.getSerializable(UserService.RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) bundle.getSerializable(UserService.RegisterTask.AUTH_TOKEN_KEY);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            observer.handleRegisterSuccess(registeredUser);
        }
    }

    public void doLogoutTask() {
        UserService.LogoutTask logoutTask = new UserService.LogoutTask(new LogoutHandler(observer));
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
    }
}
