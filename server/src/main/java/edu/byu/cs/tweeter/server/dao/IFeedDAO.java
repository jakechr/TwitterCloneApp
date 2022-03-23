package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public interface IFeedDAO {
    boolean addStatusToFeed(List<String> followerAliases, Status status);
}
