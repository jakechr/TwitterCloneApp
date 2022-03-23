package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowToggleResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    IDAOFactory daoFactory;

    public FollowService(IDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link IFollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        daoFactory.getAuthTokenDAO().authenticateCurrUserSession(request.getAuthToken());

        return getFollowDAO().getFollowees(request);
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        daoFactory.getAuthTokenDAO().authenticateCurrUserSession(request.getAuthToken());

        return getFollowDAO().getFollowers(request);
    }

    public FollowToggleResponse follow(FollowToggleRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }
        daoFactory.getAuthTokenDAO().authenticateCurrUserSession(request.getAuthToken());
        String currUserAlias = daoFactory.getAuthTokenDAO().getCurrUserAlias(request.getAuthToken());
        User currUser = daoFactory.getUserDAO().getUser(currUserAlias);

        return getFollowDAO().follow(request, currUser);
    }

    public FollowToggleResponse unfollow(FollowToggleRequest request) {
        if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }
        daoFactory.getAuthTokenDAO().authenticateCurrUserSession(request.getAuthToken());
        String currUserAlias = daoFactory.getAuthTokenDAO().getCurrUserAlias(request.getAuthToken());
        User currUser = daoFactory.getUserDAO().getUser(currUserAlias);

        return getFollowDAO().unfollow(request, currUser);
    }

    public GetFollowersCountResponse getFollowersCount(GetFollowersCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        }
        daoFactory.getAuthTokenDAO().authenticateCurrUserSession(request.getAuthToken());

        return daoFactory.getUserDAO().getFollowersCount(request);
    }

    public GetFollowingCountResponse getFollowingCount(GetFollowingCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a user alias");
        }
        daoFactory.getAuthTokenDAO().authenticateCurrUserSession(request.getAuthToken());

        return daoFactory.getUserDAO().getFollowingCount(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        System.out.println(request.toString());
        if(request.getFollowee() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a followee");
        }
        if(request.getFollower() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower");
        }
        daoFactory.getAuthTokenDAO().authenticateCurrUserSession(request.getAuthToken());

        return getFollowDAO().isFollower(request);
    }

    /**
     * Returns an instance of {@link IFollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    IFollowDAO getFollowDAO() {
        return daoFactory.getFollowsDAO();
    }
}
