package edu.byu.cs.tweeter.client.Milestone3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeIntegrationTest {
    ServerFacade serverFacade;
    User fakeUser;
    FakeData fakeData;

    @Before
    public void setup() {
        serverFacade = new ServerFacade();
        fakeUser = new User("Allen", "Anderson", "@allen", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        fakeData = new FakeData();
    }



    @Test
    public void registerTest() {
        try {
            RegisterRequest request = new RegisterRequest("@jwc", "password", "Jake",
                    "Christensen", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
            AuthenticationResponse response = serverFacade.register(request, "/register");

            assertEquals(fakeUser, response.getUser());
        } catch(Exception ex) {
            fail();
        }
    }

    @Test
    public void getFollowersTest() {
        try {
            FollowersRequest request = new FollowersRequest(new AuthToken(), "@test", 10, "@allen");
            FollowersResponse response = serverFacade.getFollowers(request, "/getfollowers");

            List<User> expected = fakeData.getFakeUsers().subList(1, 11);
            assertEquals(expected, response.getFollowers());
        } catch(Exception ex) {
            fail();
        }
    }

    @Test
    public void getFollowingCountTest() {
        try {

            GetFollowingCountRequest request = new GetFollowingCountRequest(new AuthToken(), fakeUser);
            GetFollowingCountResponse response = serverFacade.getFollowingCount(request, "/getfollowingcount");

            assertEquals(20, response.getCount());
        } catch(Exception ex) {
            fail();
        }
    }


}
