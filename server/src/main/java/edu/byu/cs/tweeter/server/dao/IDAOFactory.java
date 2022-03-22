package edu.byu.cs.tweeter.server.dao;

public interface IDAOFactory {
    IFollowDAO getFollowsDAO();
    IStatusDAO getStatusDAO();
    IUserDAO getUserDAO();
    IFeedDAO getFeedDAO();
    IAuthTokenDAO getAuthTokenDAO();
    IImageDAO getImageDAO();
}
