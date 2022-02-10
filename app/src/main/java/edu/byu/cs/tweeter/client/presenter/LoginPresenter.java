package edu.byu.cs.tweeter.client.presenter;

import android.widget.EditText;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements UserService.Observer {

    public interface View extends BasePresenter.BaseView {
        void loginSuccess(User loggedInUser);
    }

    private View view;

    public LoginPresenter(View view) {
        this.view = view;
    }

    @Override
    public void sendMessage(String message) {
        view.displayMessage(message);
    }

    @Override
    public void handleLoginSuccess(User loggedInUser) {
        view.loginSuccess(loggedInUser);
    }

    @Override
    public void handleRegisterSuccess(User registeredUser) {
        // Do nothing
    }

    public void login(EditText alias, EditText password) {
        doLoginTask(alias.getText().toString(), password.getText().toString());
    }

    public void doLoginTask(String alias, String password) {
        new UserService(this).login(alias, password);
    }
}
