package edu.byu.cs.tweeter.client.presenter;

public class LoginPresenter {

    public interface View {

    }

    private View view;

    public LoginPresenter(View view) {
        this.view = view;
    }
<<<<<<< Updated upstream
=======

    @Override
    public void sendMessage(String message) {
        view.sendMessage(message);
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
>>>>>>> Stashed changes
}
