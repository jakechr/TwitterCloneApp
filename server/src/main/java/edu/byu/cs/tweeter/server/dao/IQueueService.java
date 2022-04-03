package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;

public interface IQueueService {
    boolean addStatusToQueue(PostStatusRequest request);
}
