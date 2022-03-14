package edu.byu.cs.tweeter.client.model.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceIntegrationTest {

    private User currentUser;
    private AuthToken currentAuthToken;
    private Status lastStatus;

    private StatusService statusServiceSpy;
    private GetItemsObserver observer;

    private CountDownLatch countDownLatch;

    /**
     * Create a FollowService spy that uses a mock ServerFacade to return known responses to
     * requests.
     */
    @Before
    public void setup() {
        currentUser = new User("FirstName", "LastName", null);
        currentAuthToken = new AuthToken();
        lastStatus = new Status("post", currentUser, "date", new ArrayList<>(), new ArrayList<>());

        statusServiceSpy = Mockito.spy(new StatusService());

        // Setup an observer for the FollowService
        observer = new GetItemsObserver();

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

    /**
     * Verify that for successful requests, the {@link StatusService#loadMoreStoryItems(AuthToken, User, int, Status, PagedObserver)}
     * asynchronous method eventually returns the same result as the ServerFacade.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() throws InterruptedException {
        statusServiceSpy.loadMoreStoryItems(currentAuthToken, currentUser, 10, lastStatus, observer);
        awaitCountDownLatch();

        List<Status> expectedStatuses = new FakeData().getFakeStatuses().subList(0, 10);
        Assert.assertTrue(observer.isSuccess());
        Assert.assertNull(observer.getMessage());
        Assert.assertEquals(expectedStatuses, observer.getStatuses());
        Assert.assertTrue(observer.getHasMorePages());
        Assert.assertNull(observer.getException());
    }
}
