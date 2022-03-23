package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivityPresenter extends BasePresenter implements UserService.Observer, FollowService.Observer, StatusService.Observer {
    private static final String LOG_TAG = "MainActivityPresenter";
    private final FollowService followService;

    protected UserService getUserService() {
        return new UserService(this);
    }

    protected StatusService getStatusService() {
        return new StatusService(this);
    }

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
    public void setLoading(boolean b) {
        view.setLoading(b);
    }

    @Override
    public void handlePostStatusSuccess() {
        view.cancelPostToast();
        view.displayMessage("Successfully Posted!");
    }

    @Override
    public void handlePostStatusFailure(String msg) {
        view.cancelPostToast();
        view.displayMessage(msg);
    }

    public void doGetFollowersCountTask(User selectedUser) {
        followService.doGetFollowersCountTask(selectedUser);
    }

    public void doGetFollowingCountTask(User selectedUser) {
        followService.doGetFollowingCountTask(selectedUser);
    }

    public void doIsFollowerTask(User selectedUser) {
        followService.doIsFollowerTask(selectedUser);
    }

    public void doFollowTask(User selectedUser) {
        followService.doFollowTask(selectedUser.getAlias());
    }

    public void doUnfollowTask(User selectedUser) {
        followService.doUnfollowTask(selectedUser);
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public void doPostStatusTask(String post) {
        view.displayPostToast();
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            getStatusService().doPostStatusTask(newStatus);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public void doLogoutTask() {
        getUserService().doLogoutTask();
    }

    public interface View extends BaseView {
        void handleFollowingCountSuccess(String s);
        void cancelPostToast();
        void enableFollowButton();
        void handleUnfollowSuccess();
        void handleFollowSuccess();
        void handleFollowersCountSuccess(String s);
        void handleLogoutSuccess();
        void handleIsFollowerSuccess(boolean isFollower);
        void setLoading(boolean b);
        void displayPostToast();
    }

    private final View view;

    public MainActivityPresenter(View view) {
        super(view);
        this.view = view;
        this.followService = new FollowService(this);
    }
}
