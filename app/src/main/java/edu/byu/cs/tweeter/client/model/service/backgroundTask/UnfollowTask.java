package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowToggleRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.FollowToggleResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {
    private static final String URL_PATH = "/unfollow";
    private static final String LOG_TAG = "unfollowTask";

    /**
     * The user that is being followed.
     */
    private final User followee;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.
        try {
            FollowToggleRequest request = new FollowToggleRequest(getAuthToken(), followee);
            FollowToggleResponse response = getServerFacade().followToggle(request, URL_PATH);

            if(response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }


}
