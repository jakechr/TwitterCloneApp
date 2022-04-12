package edu.byu.cs.tweeter.server.service;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;

public class StatusServiceTest {

    private StoryRequest request;
    private StoryResponse expectedResponse;
    private IStatusDAO mockStatusDAO;
    private StatusService statusServiceSpy;

    @Before
    public void setup() {
        AuthToken authToken = new AuthToken("SselGTuIcSl1HojcRgKqTw==");

        User currentUser = new User("FirstName", "LastName", "@test", null);

        Status resultStatus1 = new Status("Test post", currentUser, DateTime.now().toString(), new ArrayList<>(Collections.singletonList("https:www.byu.edu")), new ArrayList<>(Collections.singletonList("@test2")));
        Status resultStatus2 = new Status("Test post2", currentUser, DateTime.now().toString(), new ArrayList<>(Collections.singletonList("https:www.byu.edu")), new ArrayList<>(Collections.singletonList("@test2")));
        Status resultStatus3 = new Status("Test post3", currentUser, DateTime.now().toString(), new ArrayList<>(Collections.singletonList("https:www.byu.edu")), new ArrayList<>(Collections.singletonList("@test2")));

        // Setup a request object to use in the tests
        request = new StoryRequest(authToken, currentUser.getAlias(), 3, null);

        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new StoryResponse(Arrays.asList(resultStatus1, resultStatus2, resultStatus3), false);
        mockStatusDAO = Mockito.mock(IStatusDAO.class);
        Mockito.when(mockStatusDAO.getStory(request)).thenReturn(expectedResponse);

        statusServiceSpy = Mockito.spy(new StatusService(new DynamoDAOFactory()));
        Mockito.when(statusServiceSpy.getStatusDAO()).thenReturn(mockStatusDAO);
    }

    /**
     * Verify that the {@link StatusService#getStory(StoryRequest)}
     * method returns the same result as the {@link IStatusDAO} class.
     */
    @Test
    public void testGetStory_validRequest_correctResponse() {
        StoryResponse response = statusServiceSpy.getStory(request);
        Assert.assertEquals(expectedResponse, response);
    }
}
