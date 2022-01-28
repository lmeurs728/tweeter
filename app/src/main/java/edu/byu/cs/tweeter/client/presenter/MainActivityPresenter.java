package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter implements UserService.Observer, FollowService.Observer {
    @Override
    public void handleLogoutSuccess() {
        view.handleLogoutSuccess();
    }

    @Override
    public void handleFollowersCountSuccess(String s) {
        view.handleFollowersCountSuccess(s);
    }

    @Override
    public void handleFollowingCountSuccess(String s) {
        view.handleFollowingCountSuccess(s);
    }

    @Override
    public void sendMessage(String message) {
        view.sendMessage(message);
    }

    @Override
    public void handleFailure(String message) {
        view.sendMessage(message);
    }

    public void doGetFollowersCountTask(User selectedUser) {
        new FollowService(this).doGetFollowersCountTask(selectedUser);
    }

    public void doGetFollowingCountTask(User selectedUser) {
        new FollowService(this).doGetFollowingCountTask(selectedUser);
    }

    public interface View {
        void handleLogoutSuccess();
        void sendMessage(String message);
        void handleFollowersCountSuccess(String s);
        void handleFollowingCountSuccess(String s);
    }

    private final View view;

    public MainActivityPresenter(View view) {
        this.view = view;
    }

    public void doLogoutTask() {
        new UserService(this).doLogoutTask();
    }
}
