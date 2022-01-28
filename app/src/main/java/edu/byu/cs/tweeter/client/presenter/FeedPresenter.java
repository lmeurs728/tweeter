package edu.byu.cs.tweeter.client.presenter;

import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements UserService.Observer {

    @Override
    public void handleUserSuccess(User user) {
        view.startActivity(user);
    }

    @Override
    public void sendMessage(String message) {
        view.displayMessage(message);
    }

    public interface View {
        void displayMessage(String message);
        void startActivity(User user);
    }

    private final View view;
    private final UserService userService;

    public FeedPresenter(View view) {
        this.view = view;
        userService = new UserService(this);
    }

    public void getUsersProfile(String handle) {
        userService.getUser(handle);
    }



}
