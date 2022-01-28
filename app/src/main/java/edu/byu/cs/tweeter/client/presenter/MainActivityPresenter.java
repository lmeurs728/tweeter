package edu.byu.cs.tweeter.client.presenter;

public class MainActivityPresenter {
    public interface View {

    }

    private View view;

    public MainActivityPresenter(View view) {
        this.view = view;
    }
}
