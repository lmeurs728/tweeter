package edu.byu.cs.tweeter.client.view.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
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

    @SuppressLint("StringFormatMatches")
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

        fab.setOnClickListener(view -> {
            StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
            statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
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
        }

        followButton.setOnClickListener(v -> {
            followButton.setEnabled(false);

            if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {
                presenter.doUnfollowTask(selectedUser);
                Toast.makeText(MainActivity.this, "Removing " + selectedUser.getName() + "...", Toast.LENGTH_LONG).show();
            } else {
                presenter.doFollowTask(selectedUser);
                Toast.makeText(MainActivity.this, "Adding " + selectedUser.getName() + "...", Toast.LENGTH_LONG).show();
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
        presenter.doPostStatusTask(post);
    }

    public void updateSelectedUserFollowingAndFollowers() {
        presenter.doGetFollowersCountTask(selectedUser);
        presenter.doGetFollowingCountTask(selectedUser);
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayPostToast() {
        postingToast = Toast.makeText(this, "Posting Status...", Toast.LENGTH_LONG);
        postingToast.show();
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
    public void setLoading(boolean b) {
        // do nothing
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
    public void cancelPostToast() {
        postingToast.cancel();
    }
}
