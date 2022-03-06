package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleItemObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticationHandler extends BackgroundTaskHandler<SimpleItemObserver<User>> {

    public AuthenticationHandler(SimpleItemObserver<User> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(SimpleItemObserver<User> observer, Bundle data) {
        User authenticatedUser = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);

        // Cache user session information
        Cache.getInstance().setCurrUser(authenticatedUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        observer.handleSuccess(authenticatedUser);
    }
}
