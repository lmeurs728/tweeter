package edu.byu.cs.tweeter.client.presenter;

import android.widget.EditText;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends BasePresenter implements UserService.Observer {

    public interface View extends BaseView {
        void loginSuccess(User loggedInUser);
    }

    private final View view;

    public LoginPresenter(View view) {
        super(view);
        this.view = view;
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
