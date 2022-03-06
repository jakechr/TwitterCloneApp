package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface SimpleItemObserver<T> extends ServiceObserver {
    void handleSuccess(T responseItem);

}
