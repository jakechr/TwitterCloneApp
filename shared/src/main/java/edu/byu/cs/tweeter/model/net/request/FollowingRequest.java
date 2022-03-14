package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * followees for a specified follower.
 */
public class FollowingRequest extends PagedRequest<String>{

    public FollowingRequest() {
        super();
    }

    /**
     * Creates an instance.
     *
     * @param authToken
     * @param userAlias the alias of the user whose items are to be returned.
     * @param limit     the maximum number of items to return.
     * @param lastItem  the alias of the last items that was returned in the previous request (null if
     *                  there was no previous request or if no followees were returned in the
     */
    public FollowingRequest(AuthToken authToken, String userAlias, int limit, String lastItem) {
        super(authToken, userAlias, limit, lastItem);
    }
}
