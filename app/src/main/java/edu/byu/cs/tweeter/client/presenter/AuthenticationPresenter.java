package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleItemObserver;
import edu.byu.cs.tweeter.client.presenter.View.AuthenticationView;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticationPresenter extends DescriptionPresenter<AuthenticationView>{

    public AuthenticationPresenter(AuthenticationView view) {
        super(view);
    }

    public void validateLoginInfo(String alias, String password) {
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
    }

    public class AuthenticationObserver implements SimpleItemObserver<User> {

        @Override
        public void handleSuccess(User user) {
            view.successfulLogin(user, "Hello " + Cache.getInstance().getCurrUser().getName());
        }

        @Override
        public void handleError(String message) {
            view.displayErrorMessage(getDescription() + message);
        }
    }
}
