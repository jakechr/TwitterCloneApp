package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;

public class SimpleNotificationHandler extends BackgroundTaskHandler<SimpleNotificationObserver> {

    public SimpleNotificationHandler(SimpleNotificationObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(SimpleNotificationObserver observer, Bundle data) {
        observer.handleSuccess();
    }
}
