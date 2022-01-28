package edu.byu.cs.tweeter.client.presenter;

public class StoryPresenter {
    public interface View {

    }

    private View view;

    public StoryPresenter(View view) {
        this.view = view;
    }
}
