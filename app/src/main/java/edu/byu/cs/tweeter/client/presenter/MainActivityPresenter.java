package edu.byu.cs.tweeter.client.presenter;

<<<<<<< Updated upstream
public class MainActivityPresenter {
=======
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

    public void doGetFollowersCountTask(User selectedUser) {
        new FollowService(this).doGetFollowersCountTask();
    }

    public void doGetFollowingCountTask(User selectedUser) {
        new FollowService(this).doGetFollowingCountTask();
    }

    public void doIsFollowerTask(User selectedUser) {
        new FollowService(this).doIsFollowerTask(selectedUser);
    }

    public void doFollowTask(User selectedUser) {
        new FollowService(this).doFollowTask();
    }

    public void doUnfollowTask(User selectedUser) {
        new FollowService(this).doUnfollowTask(selectedUser);
    }

    public void doPostStatusTask(Status newStatus) {
        new FollowService(this).doPostStatusTask();
    }

>>>>>>> Stashed changes
    public interface View {

    }

    private View view;

    public MainActivityPresenter(View view) {
        this.view = view;
    }
}
