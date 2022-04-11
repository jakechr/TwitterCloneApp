package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.QueueFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.QueueFollowersResponse;

public interface IQueueService {
    boolean addStatusToQueue(PostStatusRequest request);

    QueueFollowersResponse addFollowersToQueue(QueueFollowersRequest queueFollowersRequest);
}
