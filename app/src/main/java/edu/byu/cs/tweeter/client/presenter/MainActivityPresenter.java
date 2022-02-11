package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter extends BasePresenter implements UserService.Observer, FollowService.Observer {
    private final FollowService followService;

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

    public void doGetFollowersCountTask(User selectedUser) {
        followService.doGetFollowersCountTask();
    }

    public void doGetFollowingCountTask(User selectedUser) {
        followService.doGetFollowingCountTask();
    }

    public void doIsFollowerTask(User selectedUser) {
        followService.doIsFollowerTask(selectedUser);
    }

    public void doFollowTask(User selectedUser) {
        followService.doFollowTask();
    }

    public void doUnfollowTask(User selectedUser) {
        followService.doUnfollowTask(selectedUser);
    }

    public void doPostStatusTask(Status newStatus) {
        followService.doPostStatusTask();
    }

    public void doLogoutTask() {
        new UserService(this).doLogoutTask();
    }

    public interface View extends BaseView {
        void handleFollowingCountSuccess(String s);
        void handlePostStatusSuccess();
        void enableFollowButton();
        void handleUnfollowSuccess();
        void handleFollowSuccess();
        void handleFollowersCountSuccess(String s);
        void handleLogoutSuccess();
        void handleIsFollowerSuccess(boolean isFollower);
    }

    private final View view;

    public MainActivityPresenter(View view) {
        super(view);
        this.view = view;
        this.followService = new FollowService(this);
    }
}
