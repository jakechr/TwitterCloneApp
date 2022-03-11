package edu.byu.cs.tweeter.client.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.View.MainView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {

    private MainView mockView;
    private StatusService mockStatusService;
    private Cache mockCache;
    private Status mockStatus;
    private AuthToken mockAuthToken;

    private MainPresenter mainPresenterSpy;

    @Before
    public void setup() {
        // Create Mocks
        mockView = Mockito.mock(MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);
        mockStatus = Mockito.mock(Status.class);
        mockAuthToken = Mockito.mock(AuthToken.class);
        Cache.setInstance(mockCache);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
        Mockito.when(mockCache.getCurrUserAuthToken()).thenReturn(mockAuthToken);
    }

    @Test
    public void testPostStatus_PostStatusSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotificationObserver observer = invocation.getArgument(2, SimpleNotificationObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        callServiceAndVerifyDisplayInfo(answer);
        Mockito.verify(mockView).handlePostStatusSuccess();
        Mockito.verify(mockView).clearInfoMessage();
    }

    @Test
    public void testPostStatus_PostStatusFailedWithMessage() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotificationObserver observer = invocation.getArgument(2, SimpleNotificationObserver.class);
                observer.handleError(": Error Message!");
                return null;
            }
        };

        callServiceAndVerifyDisplayInfo(answer);
        verifyErrorResult("Failed to post status: Error Message!");
    }

    @Test
    public void testPostStatus_PostStatusFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotificationObserver observer = invocation.getArgument(2, SimpleNotificationObserver.class);
                observer.handleError(" because of exception: Exception Message!");
                return null;
            }
        };

        callServiceAndVerifyDisplayInfo(answer);
        verifyErrorResult("Failed to post status because of exception: Exception Message!");
    }

    private void callServiceAndVerifyDisplayInfo(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.eq(mockAuthToken), Mockito.eq(mockStatus), Mockito.any());
        mainPresenterSpy.postStatus(mockStatus);

        Mockito.verify(mockView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockStatusService).postStatus(Mockito.eq(mockAuthToken), Mockito.eq(mockStatus), Mockito.notNull());
    }

    private void verifyErrorResult(String message) {
        Mockito.verify(mockView).displayErrorMessage(message);
        Mockito.verify(mockView).clearInfoMessage();
    }

}
