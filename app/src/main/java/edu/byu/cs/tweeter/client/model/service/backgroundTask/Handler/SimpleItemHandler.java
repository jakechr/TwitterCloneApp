package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleItemObserver;

/**
 * Message handler (i.e., observer) for GetUserTask.
 */
public class SimpleItemHandler<T> extends BackgroundTaskHandler<SimpleItemObserver<T>> {

    public SimpleItemHandler(SimpleItemObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(SimpleItemObserver<T> observer, Bundle data) {
        T responseItem = (T) data.getSerializable(BackgroundTask.SIMPLE_ITEM_KEY);
        observer.handleSuccess(responseItem);
    }
}