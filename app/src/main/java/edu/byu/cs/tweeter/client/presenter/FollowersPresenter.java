package edu.byu.cs.tweeter.client.presenter;

public class FollowersPresenter {

    public interface View {

    }

    private View view;

    public FollowersPresenter(View view) {
        this.view = view;
    }
}
