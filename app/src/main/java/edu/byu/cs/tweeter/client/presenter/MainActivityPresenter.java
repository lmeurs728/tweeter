package edu.byu.cs.tweeter.client.presenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
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
    public void handleIsFollowerSuccess(boolean isFollower) {
        view.handleIsFollowerSuccess(isFollower);
    }

    @Override
    public void handleFollowSuccess() {
        view.handleFollowSuccess();
    }

    @Override
    public void enableFollowButton() {
        view.enableFollowButton();
    }

    @Override
    public void handleUnfollowSuccess() {
        view.handleUnfollowSuccess();
    }

    @Override
    public void handlePostStatusSuccess() {
        view.handlePostStatusSuccess();
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

    public void doIsFollowerTask(User selectedUser) {
        new FollowService(this).doIsFollowerTask(selectedUser);
    }

    public void doFollowTask(User selectedUser) {
        new FollowService(this).doFollowTask(selectedUser);
    }

    public void doUnfollowTask(User selectedUser) {
        new FollowService(this).doUnfollowTask(selectedUser);
    }

    public void doPostStatusTask(Status newStatus) {
        new FollowService(this).doPostStatusTask(newStatus);
    }

    public interface View {
        void handleLogoutSuccess();
        void sendMessage(String message);
        void handleFollowersCountSuccess(String s);
        void handleFollowingCountSuccess(String s);
        void handleIsFollowerSuccess(boolean isFollower);
        void handleFollowSuccess();
        void enableFollowButton();
        void handleUnfollowSuccess();
        void handlePostStatusSuccess();
    }

    private final View view;

    public MainActivityPresenter(View view) {
        this.view = view;
    }

    public void doLogoutTask() {
        new UserService(this).doLogoutTask();
    }
}
