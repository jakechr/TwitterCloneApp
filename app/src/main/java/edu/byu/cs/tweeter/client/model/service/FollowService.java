package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.SimpleItemHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleItemObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends PagedService {

    /**
     * Creates an instance.
     */
    public FollowService() {}

    /**
     * Requests the users that the user specified in the request is following.
     * Limits the number of followees returned and returns the next set of
     * followees after any that were returned in a previous request.
     * This is an asynchronous operation.
     *
     */
    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken, user, pageSize, lastFollowee, new PagedHandler<>(getFollowingObserver));
        baseService.executeTask(getFollowingTask);
    }

    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PagedHandler<>(getFollowersObserver));
        baseService.executeTask(getFollowersTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(unfollowObserver));
        baseService.executeTask(unfollowTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(followObserver));
        baseService.executeTask(followTask);
    }


    public void isFollower(AuthToken currUserAuthToken, User selectedUser, User currUser, SimpleItemObserver<Boolean> isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken,
                currUser, selectedUser, new SimpleItemHandler<>(isFollowerObserver));
        baseService.executeTask(isFollowerTask);
    }

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, SimpleItemObserver<Integer> getFollowersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new SimpleItemHandler<>(getFollowersCountObserver));
        baseService.executeTask(followersCountTask);
    }


    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, SimpleItemObserver<Integer> getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new SimpleItemHandler<>(getFollowingCountObserver));
        baseService.executeTask(followingCountTask);
    }


}
