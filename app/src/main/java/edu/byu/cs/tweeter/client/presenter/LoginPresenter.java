package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.View.AuthenticationView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;

/**
 * The presenter for the login functionality of the application.
 */
public class LoginPresenter extends AuthenticationPresenter {

    private static final String LOG_TAG = "LoginPresenter";

    private UserService userService;

    public LoginPresenter(AuthenticationView view) {
        super(view);
        this.userService = new UserService();
    }

    @Override
    String getDescription() {
        return "Failed to login";
    }

    public void login(String userAlias, String password) {
        userService.login(userAlias, password, new AuthenticationObserver());
    }
}
