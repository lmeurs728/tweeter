package edu.byu.cs.tweeter.client.presenter;

import android.widget.EditText;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements UserService.Observer {

    public void validateLogin(EditText alias, EditText password) {
        if (alias.getText().charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.getText().length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.getText().length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public interface View {
        void loginSuccess(User user);

        void sendMessage(String message);
    }

    private final View view;

    public LoginPresenter(View view) {
        this.view = view;
    }

    @Override
    public void handleUserSuccess(User user) {
        // Do nothing
    }

    @Override
    public void sendMessage(String message) {
        view.sendMessage(message);
    }

    @Override
    public void handleLoginSuccess(User loggedInUser) {
        view.loginSuccess(loggedInUser);
    }

    public void login(EditText alias, EditText password) {
        doLoginTask(alias.getText().toString(), password.getText().toString());
    }

    public void doLoginTask(String alias, String password) {
        new UserService(this).login(alias, password);
    }
}
