package edu.byu.cs.tweeter.client.presenter.View;

import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticationView extends BaseView{
    void successfulLogin(User user, String message);
}
