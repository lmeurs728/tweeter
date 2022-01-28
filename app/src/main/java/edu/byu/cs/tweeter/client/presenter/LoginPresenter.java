package edu.byu.cs.tweeter.client.presenter;

public class LoginPresenter {

    public interface View {

    }

    private View view;

    public LoginPresenter(View view) {
        this.view = view;
    }
}
