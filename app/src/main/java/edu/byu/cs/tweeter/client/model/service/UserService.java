package edu.byu.cs.tweeter.client.model.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class UserService {

    private final Observer observer;

    public UserService(Observer observer) {
        this.observer = observer;
    }

    public interface Observer {
        default void handleUserSuccess(User user) {
            // do nothing
        }
        void sendMessage(String message);

        default void handleLoginSuccess(User loggedInUser) {
            // do nothing
        }

        default void handleRegisterSuccess(User registeredUser) {
            // do nothing
        }

        default void handleLogoutSuccess() {
            // do nothing
        }
    }

    public void getUser(String clickable) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                clickable, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
        observer.sendMessage("Getting user's profile...");
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private static class GetUserHandler extends Handler {

        private final Observer observer;

        public GetUserHandler(Observer observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
                observer.handleUserSuccess(user);
            }
            else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                observer.sendMessage("Failed to get user's profile: " + message);
            }
            else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                observer.sendMessage("Failed to get user's profile because of exception: " + ex.getMessage());
            }
        }
    }

    /**
     * Background task that returns the profile for a specified user.
     */
    public static class GetUserTask implements Runnable {
        private static final String LOG_TAG = "GetUserTask";

        public static final String SUCCESS_KEY = "success";
        public static final String USER_KEY = "user";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * Alias (or handle) for user whose profile is being retrieved.
         */
        private String alias;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
            this.authToken = authToken;
            this.alias = alias;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {
                User user = getUser();

                sendSuccessMessage(user);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private User getUser() {
            User user = getFakeData().findUserByAlias(alias);
            return user;
        }

        private void sendSuccessMessage(User user) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putSerializable(USER_KEY, user);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    /**
     * Background task that logs in a user (i.e., starts a session).
     */
    public static class LoginTask implements Runnable {

        private static final String LOG_TAG = "LoginTask";

        public static final String SUCCESS_KEY = "success";
        public static final String USER_KEY = "user";
        public static final String AUTH_TOKEN_KEY = "auth-token";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * The user's username (or "alias" or "handle"). E.g., "@susan".
         */
        private String username;
        /**
         * The user's password.
         */
        private String password;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public LoginTask(String username, String password, Handler messageHandler) {
            this.username = username;
            this.password = password;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {
                Pair<User, AuthToken> loginResult = doLogin();

                User loggedInUser = loginResult.getFirst();
                AuthToken authToken = loginResult.getSecond();

                sendSuccessMessage(loggedInUser, authToken);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private Pair<User, AuthToken> doLogin() {
            User loggedInUser = getFakeData().getFirstUser();
            AuthToken authToken = getFakeData().getAuthToken();
            return new Pair<>(loggedInUser, authToken);
        }

        private void sendSuccessMessage(User loggedInUser, AuthToken authToken) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putSerializable(USER_KEY, loggedInUser);
            msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    /**
     * Background task that logs out a user (i.e., ends a session).
     */
    public static class LogoutTask implements Runnable {
        private static final String LOG_TAG = "LogoutTask";

        public static final String SUCCESS_KEY = "success";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         */
        private AuthToken authToken;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public LogoutTask(AuthToken authToken, Handler messageHandler) {
            this.authToken = authToken;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {

                sendSuccessMessage();

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private void sendSuccessMessage() {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    /**
     * Background task that creates a new user account and logs in the new user (i.e., starts a session).
     */
    public static class RegisterTask implements Runnable {
        private static final String LOG_TAG = "RegisterTask";

        public static final String SUCCESS_KEY = "success";
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
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public RegisterTask(String firstName, String lastName, String username, String password,
                            String image, Handler messageHandler) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.image = image;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {
                Pair<User, AuthToken> registerResult = doRegister();

                User registeredUser = registerResult.getFirst();
                AuthToken authToken = registerResult.getSecond();

                sendSuccessMessage(registeredUser, authToken);

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private FakeData getFakeData() {
            return new FakeData();
        }

        private Pair<User, AuthToken> doRegister() {
            User registeredUser = getFakeData().getFirstUser();
            AuthToken authToken = getFakeData().getAuthToken();
            return new Pair<>(registeredUser, authToken);
        }

        private void sendSuccessMessage(User registeredUser, AuthToken authToken) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);
            msgBundle.putSerializable(USER_KEY, registeredUser);
            msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    /**
     * Background task that removes a following relationship between two users.
     */
    public static class UnfollowTask implements Runnable {
        private static final String LOG_TAG = "UnfollowTask";

        public static final String SUCCESS_KEY = "success";
        public static final String MESSAGE_KEY = "message";
        public static final String EXCEPTION_KEY = "exception";

        /**
         * Auth token for logged-in user.
         * This user is the "follower" in the relationship.
         */
        private AuthToken authToken;
        /**
         * The user that is being followed.
         */
        private User followee;
        /**
         * Message handler that will receive task results.
         */
        private Handler messageHandler;

        public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
            this.authToken = authToken;
            this.followee = followee;
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            try {

                sendSuccessMessage();

            } catch (Exception ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
                sendExceptionMessage(ex);
            }
        }

        private void sendSuccessMessage() {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, true);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendFailedMessage(String message) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putString(MESSAGE_KEY, message);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }

        private void sendExceptionMessage(Exception exception) {
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(SUCCESS_KEY, false);
            msgBundle.putSerializable(EXCEPTION_KEY, exception);

            Message msg = Message.obtain();
            msg.setData(msgBundle);

            messageHandler.sendMessage(msg);
        }
    }

    public LoginTask getLoginTask(String alias, String password) {
        return new LoginTask(alias, password, new LoginHandler(observer));
    }

    public void login(String alias, String password) {
        // Send the login request.
        LoginTask loginTask = getLoginTask(alias, password);
        BackgroundTaskUtils.runTask(loginTask);
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private class LoginHandler extends Handler {
        Observer observer;
        
        public LoginHandler(Observer observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UserService.LoginTask.SUCCESS_KEY);
            if (success) {
                User loggedInUser = (User) msg.getData().getSerializable(UserService.LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(UserService.LoginTask.AUTH_TOKEN_KEY);

                // Cache user session information
                Cache.getInstance().setCurrUser(loggedInUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                observer.handleLoginSuccess(loggedInUser);
            } else if (msg.getData().containsKey(UserService.LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UserService.LoginTask.MESSAGE_KEY);
                observer.sendMessage("Failed to login: " + message);
            } else if (msg.getData().containsKey(UserService.LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UserService.LoginTask.EXCEPTION_KEY);
                observer.sendMessage("Failed to login because of exception: " + ex.getMessage());
            }
        }
    }

    public RegisterTask getRegisterTask(String firstName, String lastName, String username, String password, String image) {
        return new RegisterTask(firstName, lastName, username, password, image, new RegisterHandler(observer));
    }

    public void register(String firstName, String lastName, String username, String password, String image) {
        // Send register request.
        RegisterTask registerTask = getRegisterTask(firstName, lastName, username, password, image);
        BackgroundTaskUtils.runTask(registerTask);
    }

    private class RegisterHandler extends Handler {
        private final Observer observer;

        public RegisterHandler(Observer observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UserService.RegisterTask.SUCCESS_KEY);
            if (success) {
                User registeredUser = (User) msg.getData().getSerializable(UserService.RegisterTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(UserService.RegisterTask.AUTH_TOKEN_KEY);

                Cache.getInstance().setCurrUser(registeredUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                observer.handleRegisterSuccess(registeredUser);

//                Intent intent = new Intent(getContext(), MainActivity.class);
//
//                intent.putExtra(MainActivity.CURRENT_USER_KEY, registeredUser);
//
//                registeringToast.cancel();
//
//                Toast.makeText(getContext(), "Hello " + Cache.getInstance().getCurrUser().getName(), Toast.LENGTH_LONG).show();
//                try {
//                    startActivity(intent);
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
            } else if (msg.getData().containsKey(UserService.RegisterTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UserService.RegisterTask.MESSAGE_KEY);
                observer.sendMessage("Failed to register: " + message);
            } else if (msg.getData().containsKey(UserService.RegisterTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UserService.RegisterTask.EXCEPTION_KEY);
                observer.sendMessage("Failed to register because of exception: " + ex.getMessage());
            }
        }
    }

    public void doLogoutTask() {
        UserService.LogoutTask logoutTask = new UserService.LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler());
        BackgroundTaskUtils.runTask(logoutTask);
    }

    // LogoutHandler

    private class LogoutHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UserService.LogoutTask.SUCCESS_KEY);
            if (success) {
                observer.handleLogoutSuccess();
//                logOutToast.cancel();
//                logoutUser();
            } else if (msg.getData().containsKey(UserService.LogoutTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UserService.LogoutTask.MESSAGE_KEY);
                observer.sendMessage("Failed to logout: " + message);
//                Toast.makeText(MainActivity.this, "Failed to logout: " + message, Toast.LENGTH_LONG).show();
            } else if (msg.getData().containsKey(UserService.LogoutTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UserService.LogoutTask.EXCEPTION_KEY);
                observer.sendMessage("Failed to logout because of exception: " + ex.getMessage());
//                Toast.makeText(MainActivity.this, "Failed to logout because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
