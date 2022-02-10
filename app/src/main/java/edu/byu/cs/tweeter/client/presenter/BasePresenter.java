package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.BaseObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class BasePresenter implements BaseObserver, UserService.Observer {
    private final BaseView view;

    public BasePresenter(BaseView view) {
        this.view = view;
    }

    @Override
    public void sendMessage(String message) {
        view.displayMessage(message);
    }

    public interface BaseView {
        void displayMessage(String message);
        void startActivity(User user);
    }

    public void getUsersProfile(String handle) {
        new UserService(this).getUser(handle);
    }
}
