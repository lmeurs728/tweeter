package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer, MainActivityPresenter.View {

    private static final String LOG_TAG = "MainActivity";

    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast logOutToast;
    private Toast postingToast;
    private User selectedUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;

    private MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });
        presenter = new MainActivityPresenter(this);

        updateSelectedUserFollowingAndFollowers();

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        Picasso.get().load(selectedUser.getImageUrl()).into(userImageView);

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            presenter.doIsFollowerTask(selectedUser);
//            FollowService.IsFollowerTask isFollowerTask = new FollowService.IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
//                    Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler());
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.execute(isFollowerTask);
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButton.setEnabled(false);

                if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {
                    presenter.doUnfollowTask(selectedUser);
//                    UserService.UnfollowTask unfollowTask = new UserService.UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
//                            selectedUser, new UnfollowHandler());
//                    ExecutorService executor = Executors.newSingleThreadExecutor();
//                    executor.execute(unfollowTask);

                    Toast.makeText(MainActivity.this, "Removing " + selectedUser.getName() + "...", Toast.LENGTH_LONG).show();
                } else {
                    presenter.doFollowTask(selectedUser);
//                    FollowService.FollowTask followTask = new FollowService.FollowTask(Cache.getInstance().getCurrUserAuthToken(),
//                            selectedUser, new FollowHandler());
//                    ExecutorService executor = Executors.newSingleThreadExecutor();
//                    executor.execute(followTask);

                    Toast.makeText(MainActivity.this, "Adding " + selectedUser.getName() + "...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            logOutToast = Toast.makeText(this, "Logging Out...", Toast.LENGTH_LONG);
            logOutToast.show();

//            UserService.LogoutTask logoutTask = new UserService.LogoutTask(Cache.getInstance().getCurrUserAuthToken(), new LogoutHandler());
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.execute(logoutTask);
            presenter.doLogoutTask();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void logoutUser() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Clear user data (cached data).
        Cache.getInstance().clearCache();
        startActivity(intent);
    }

    @Override
    public void onStatusPosted(String post) {
        postingToast = Toast.makeText(this, "Posting Status...", Toast.LENGTH_LONG);
        postingToast.show();

        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            presenter.doPostStatusTask(newStatus);
//            StatusService.PostStatusTask statusTask = new StatusService.PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
//                    newStatus, new PostStatusHandler());
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.execute(statusTask);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            Toast.makeText(this, "Failed to post the status because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
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

    public void updateSelectedUserFollowingAndFollowers() {
//        ExecutorService executor = Executors.newFixedThreadPool(2);

        presenter.doGetFollowersCountTask(selectedUser);
//        // Get count of most recently selected user's followers.
//        FollowService.GetFollowersCountTask followersCountTask = new FollowService.GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
//                selectedUser, new GetFollowersCountHandler());
//        executor.execute(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        presenter.doGetFollowingCountTask(selectedUser);
//        FollowService.GetFollowingCountTask followingCountTask = new FollowService.GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
//                selectedUser, new GetFollowingCountHandler());
//        executor.execute(followingCountTask);
    }

    public void updateFollowButton(boolean removed) {
        // If follow relationship was removed.
        if (removed) {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        }
    }

    @Override
    public void handleLogoutSuccess() {
        logOutToast.cancel();
        logoutUser();
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startActivity(User user) {
        // Do nothing
    }

    @Override
    public void handleFollowersCountSuccess(String s) {
        followerCount.setText(getString(R.string.followerCount, s));
    }

    @Override
    public void handleFollowingCountSuccess(String s) {
        followeeCount.setText(getString(R.string.followeeCount, s));
    }

    @Override
    public void handleIsFollowerSuccess(boolean isFollower) {
        // If logged in user if a follower of the selected user, display the follow button as "following"
        if (isFollower) {
            followButton.setText(R.string.following);
            followButton.setBackgroundColor(getResources().getColor(R.color.white));
            followButton.setTextColor(getResources().getColor(R.color.lightGray));
        } else {
            followButton.setText(R.string.follow);
            followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public void handleFollowSuccess() {
        updateSelectedUserFollowingAndFollowers();
        updateFollowButton(false);
    }

    @Override
    public void enableFollowButton() {
        followButton.setEnabled(true);
    }

    @Override
    public void handleUnfollowSuccess() {
        updateSelectedUserFollowingAndFollowers();
        updateFollowButton(true);
    }

    @Override
    public void handlePostStatusSuccess() {
        postingToast.cancel();
        Toast.makeText(MainActivity.this, "Successfully Posted!", Toast.LENGTH_LONG).show();
    }

//    // LogoutHandler
//
//    private class LogoutHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            boolean success = msg.getData().getBoolean(UserService.LogoutTask.SUCCESS_KEY);
//            if (success) {
//                logOutToast.cancel();
//                logoutUser();
//            } else if (msg.getData().containsKey(UserService.LogoutTask.MESSAGE_KEY)) {
//                String message = msg.getData().getString(UserService.LogoutTask.MESSAGE_KEY);
//                Toast.makeText(MainActivity.this, "Failed to logout: " + message, Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(UserService.LogoutTask.EXCEPTION_KEY)) {
//                Exception ex = (Exception) msg.getData().getSerializable(UserService.LogoutTask.EXCEPTION_KEY);
//                Toast.makeText(MainActivity.this, "Failed to logout because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    // GetFollowersCountHandler
//
//    private class GetFollowersCountHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            boolean success = msg.getData().getBoolean(FollowService.GetFollowersCountTask.SUCCESS_KEY);
//            if (success) {
//                int count = msg.getData().getInt(FollowService.GetFollowersCountTask.COUNT_KEY);
//                followerCount.setText(getString(R.string.followerCount, String.valueOf(count)));
//            } else if (msg.getData().containsKey(FollowService.GetFollowersCountTask.MESSAGE_KEY)) {
//                String message = msg.getData().getString(FollowService.GetFollowersCountTask.MESSAGE_KEY);
//                Toast.makeText(MainActivity.this, "Failed to get followers count: " + message, Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(FollowService.GetFollowersCountTask.EXCEPTION_KEY)) {
//                Exception ex = (Exception) msg.getData().getSerializable(FollowService.GetFollowersCountTask.EXCEPTION_KEY);
//                Toast.makeText(MainActivity.this, "Failed to get followers count because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    // GetFollowingCountHandler
//
//    private class GetFollowingCountHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            boolean success = msg.getData().getBoolean(FollowService.GetFollowingCountTask.SUCCESS_KEY);
//            if (success) {
//                int count = msg.getData().getInt(FollowService.GetFollowingCountTask.COUNT_KEY);
//                followeeCount.setText(getString(R.string.followeeCount, String.valueOf(count)));
//            } else if (msg.getData().containsKey(FollowService.GetFollowingCountTask.MESSAGE_KEY)) {
//                String message = msg.getData().getString(FollowService.GetFollowingCountTask.MESSAGE_KEY);
//                Toast.makeText(MainActivity.this, "Failed to get following count: " + message, Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(FollowService.GetFollowingCountTask.EXCEPTION_KEY)) {
//                Exception ex = (Exception) msg.getData().getSerializable(FollowService.GetFollowingCountTask.EXCEPTION_KEY);
//                Toast.makeText(MainActivity.this, "Failed to get following count because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    // IsFollowerHandler
//
//    private class IsFollowerHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            boolean success = msg.getData().getBoolean(FollowService.IsFollowerTask.SUCCESS_KEY);
//            if (success) {
//                boolean isFollower = msg.getData().getBoolean(FollowService.IsFollowerTask.IS_FOLLOWER_KEY);
//
//                // If logged in user if a follower of the selected user, display the follow button as "following"
//                if (isFollower) {
//                    followButton.setText(R.string.following);
//                    followButton.setBackgroundColor(getResources().getColor(R.color.white));
//                    followButton.setTextColor(getResources().getColor(R.color.lightGray));
//                } else {
//                    followButton.setText(R.string.follow);
//                    followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                }
//            } else if (msg.getData().containsKey(FollowService.IsFollowerTask.MESSAGE_KEY)) {
//                String message = msg.getData().getString(FollowService.IsFollowerTask.MESSAGE_KEY);
//                Toast.makeText(MainActivity.this, "Failed to determine following relationship: " + message, Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(FollowService.IsFollowerTask.EXCEPTION_KEY)) {
//                Exception ex = (Exception) msg.getData().getSerializable(FollowService.IsFollowerTask.EXCEPTION_KEY);
//                Toast.makeText(MainActivity.this, "Failed to determine following relationship because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    // FollowHandler
//
//    private class FollowHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            boolean success = msg.getData().getBoolean(FollowService.FollowTask.SUCCESS_KEY);
//            if (success) {
//                updateSelectedUserFollowingAndFollowers();
//                updateFollowButton(false);
//            } else if (msg.getData().containsKey(FollowService.FollowTask.MESSAGE_KEY)) {
//                String message = msg.getData().getString(FollowService.FollowTask.MESSAGE_KEY);
//                Toast.makeText(MainActivity.this, "Failed to follow: " + message, Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(FollowService.FollowTask.EXCEPTION_KEY)) {
//                Exception ex = (Exception) msg.getData().getSerializable(FollowService.FollowTask.EXCEPTION_KEY);
//                Toast.makeText(MainActivity.this, "Failed to follow because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//
//            followButton.setEnabled(true);
//        }
//    }

//    // UnfollowHandler
//
//    private class UnfollowHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            boolean success = msg.getData().getBoolean(UserService.UnfollowTask.SUCCESS_KEY);
//            if (success) {
//                updateSelectedUserFollowingAndFollowers();
//                updateFollowButton(true);
//            } else if (msg.getData().containsKey(UserService.UnfollowTask.MESSAGE_KEY)) {
//                String message = msg.getData().getString(UserService.UnfollowTask.MESSAGE_KEY);
//                Toast.makeText(MainActivity.this, "Failed to unfollow: " + message, Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(UserService.UnfollowTask.EXCEPTION_KEY)) {
//                Exception ex = (Exception) msg.getData().getSerializable(UserService.UnfollowTask.EXCEPTION_KEY);
//                Toast.makeText(MainActivity.this, "Failed to unfollow because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//
//            followButton.setEnabled(true);
//        }
//    }

//    // PostStatusHandler
//
//    private class PostStatusHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            boolean success = msg.getData().getBoolean(StatusService.PostStatusTask.SUCCESS_KEY);
//            if (success) {
//                postingToast.cancel();
//                Toast.makeText(MainActivity.this, "Successfully Posted!", Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(StatusService.PostStatusTask.MESSAGE_KEY)) {
//                String message = msg.getData().getString(StatusService.PostStatusTask.MESSAGE_KEY);
//                Toast.makeText(MainActivity.this, "Failed to post status: " + message, Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(StatusService.PostStatusTask.EXCEPTION_KEY)) {
//                Exception ex = (Exception) msg.getData().getSerializable(StatusService.PostStatusTask.EXCEPTION_KEY);
//                Toast.makeText(MainActivity.this, "Failed to post status because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

}
