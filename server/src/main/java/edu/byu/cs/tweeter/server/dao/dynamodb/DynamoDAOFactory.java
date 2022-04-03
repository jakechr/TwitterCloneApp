package edu.byu.cs.tweeter.server.dao.dynamodb;

import edu.byu.cs.tweeter.server.dao.IAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IImageDAO;
import edu.byu.cs.tweeter.server.dao.IQueueService;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public class DynamoDAOFactory implements IDAOFactory {
    @Override
    public IFollowDAO getFollowsDAO() {
        return new FollowsDAODynamo();
    }

    @Override
    public IStatusDAO getStatusDAO() {
        return new StatusDAODynamo();
    }

    @Override
    public IUserDAO getUserDAO() {
        return new UserDAODynamo();
    }

    @Override
    public IFeedDAO getFeedDAO() {
        return new FeedDAODynamo();
    }

    @Override
    public IAuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDAODynamo();
    }

    @Override
    public IImageDAO getImageDAO() {
        return new ImageDAOS3();
    }

    @Override
    public IQueueService getQueueService() {
        return new QueueServiceSQS();
    }
}
