package edu.byu.cs.tweeter.server.dao;

public interface IDAOFactory {
    public IFollowDAO getFollowDAO();
    public IStatusDAO getStatusDAO();
    public IUserDAO getUserDAO();
}
