package edu.byu.cs.tweeter.client.Milestone4C;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.StatusServiceIntegrationTest;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleItemObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.presenter.View.AuthenticationView;
import edu.byu.cs.tweeter.client.presenter.View.MainView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class IntegrationTest {

    private User currentUser;
    private AuthToken currentAuthToken;
    private Status lastStatus;

    private StatusService statusServiceSpy;
    private UserService userServiceSpy;
    private GetItemsObserver observer;
    private AuthenticationObserver loginObserver;
    private PostStatusObserver postStatusObserver;

    private MainPresenter mainPresenterSpy;
    private PostStatusView postStatusViewSpy;

    private CountDownLatch countDownLatch;

    /**
     * Create a FollowService spy that uses a mock ServerFacade to return known responses to
     * requests.
     */
    @Before
    public void setup() {
        currentUser = new User("Jake", "Christensen", "@jake", "https://tweeterapp340.s3.amazonaws.com/%40jake");
        currentAuthToken = null;
        lastStatus = new Status("post", currentUser, LocalDateTime.now().toString(), new ArrayList<>(), new ArrayList<>());

        statusServiceSpy = Mockito.spy(new StatusService());
        userServiceSpy = Mockito.spy(new UserService());
        postStatusViewSpy = Mockito.spy(new PostStatusView());
        mainPresenterSpy = Mockito.spy(new MainPresenter(postStatusViewSpy));

        // Setup an observer for the StatusService
        observer = new GetItemsObserver();
        loginObserver = new AuthenticationObserver();
        postStatusObserver = new PostStatusObserver();




        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }



    /**
     * An Observer implementation that can be used to get the values
     * eventually returned by an asynchronous call on the {@link StatusService}. Counts down
     * on the countDownLatch so tests can wait for the background thread to call a method on the
     * observer.
     */
    private class GetItemsObserver implements PagedObserver<Status> {

        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.statuses = items;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleError(String message) {
            this.success = false;
            this.message = message;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }


        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
    }

    public class AuthenticationObserver implements SimpleItemObserver<User> {

        private boolean success;
        private String message;

        @Override
        public void handleSuccess(User user) {
            this.success = true;
            this.message = null;
            countDownLatch.countDown();
        }

        @Override
        public void handleError(String message) {
            this.success = false;
            this.message = message;
            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public class PostStatusObserver implements SimpleNotificationObserver {

        private boolean success;
        private String message;

        @Override
        public void handleSuccess() {
            this.success = true;
            this.message = null;
            countDownLatch.countDown();
            postStatusViewSpy.handlePostStatusSuccess();
        }

        @Override
        public void handleError(String message) {
            this.success = false;
            this.message = message;
            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public class PostStatusView implements MainView {
        @Override
        public void displayErrorMessage(String message) {
            countDownLatch.countDown();
        }

        @Override
        public void handleUnfollowSuccess() {

        }

        @Override
        public void handleFollowSuccess() {

        }

        @Override
        public void handleLogoutSuccess() {

        }

        @Override
        public void handleGetFollowersCountSuccess(int count) {

        }

        @Override
        public void handleGetFollowingCountSuccess(int count) {

        }

        @Override
        public void handleIsFollowerSuccess(boolean isFollower) {

        }

        @Override
        public void handlePostStatusSuccess() {
            countDownLatch.countDown();
            System.out.println("Successfully Posted!");
        }

        @Override
        public void resetFollowButton() {

        }

        @Override
        public void displayInfoMessage(String message) {

        }

        @Override
        public void clearInfoMessage() {

        }
    }


    /**
     * Verify that for successful requests, the {@link StatusService#loadMoreStoryItems(AuthToken, User, int, Status, PagedObserver)}
     * asynchronous method eventually returns the same result as the ServerFacade.
     */
    @Test
    public void testGetStory_validRequest_correctResponse() throws InterruptedException {
        userServiceSpy.login("@jake", "password", loginObserver);
        awaitCountDownLatch();

        currentAuthToken = Cache.getInstance().getCurrUserAuthToken();

        mainPresenterSpy.postStatus(lastStatus);
        awaitCountDownLatch();

        statusServiceSpy.loadMoreStoryItems(currentAuthToken, currentUser, 10, null, observer);
        awaitCountDownLatch();

        Mockito.verify(postStatusViewSpy).handlePostStatusSuccess();
        Assert.assertTrue(observer.isSuccess());
        Assert.assertNull(observer.getMessage());
        Assert.assertEquals(lastStatus, observer.getStatuses().get(observer.getStatuses().size() - 1));
        Assert.assertFalse(observer.getHasMorePages());
        Assert.assertNull(observer.getException());
    }
}
